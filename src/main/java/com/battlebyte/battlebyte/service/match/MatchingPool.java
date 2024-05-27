package com.battlebyte.battlebyte.service.match;

import com.battlebyte.battlebyte.config.BeanContext;
import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.service.GameService;
import com.battlebyte.battlebyte.service.OJService;
import com.battlebyte.battlebyte.websocket.CurrentGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.battlebyte.battlebyte.service.MatchService.returnMatchResult;

/**
 * 匹配池
 */
@Configurable
public class MatchingPool extends Thread {

    private GameService gameService;
    private OJService ojService;
    private static List<Player> royalePlayers = new ArrayList<>();
    private static List<Player> oneToOnePlayers = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;

    private static int count1 = 0;
    private static int count2 = 0;

    public MatchingPool() {
        this.gameService = BeanContext.getApplicationContext().getBean(GameService.class);
        this.ojService = BeanContext.getApplicationContext().getBean(OJService.class);
    }

    //排序
    private Comparator<Player> playerComparator = new Comparator<Player>() {
        @Override
        public int compare(Player p1, Player p2) {
            // 根据Player对象的变量i进行比较
            return Double.compare(p1.getRating() * 0.1 + p1.getWaitingTime() * 50, p2.getRating() * 0.1 + p2.getWaitingTime() * 50);
        }
    };

    //单人模式
    public void addPlayer1(Integer userId, Integer rating) {
        lock.lock();
        try {
            oneToOnePlayers.add(new Player(userId, rating, 0));
        } finally {
            lock.unlock();
        }
    }

    //大逃杀模式
    public void addPlayer2(Integer userId, Integer rating) {
        lock.lock();
        try {
            royalePlayers.add(new Player(userId, rating, 0));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 在匹配池中删除一个玩家
     *
     * @param userId
     */
    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            //删除1v1模式
            List<Player> newPlayers1 = new ArrayList<>();
            for (Player player : oneToOnePlayers) {
                if (!player.getUserId().equals(userId)) {
                    newPlayers1.add(player);
                }
            }
            oneToOnePlayers = newPlayers1;

            //删除大逃杀模式
            List<Player> newPlayers2 = new ArrayList<>();
            for (Player player : royalePlayers) {
                if (!player.getUserId().equals(userId)) {
                    newPlayers2.add(player);
                }
            }
            royalePlayers = newPlayers2;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 线程的作用：每秒钟匹配一下所有玩家
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                lock.lock();

                System.out.println("current 1v1 pool players num:" + oneToOnePlayers.size());
                System.out.println("current Royale pool players num:" + royalePlayers.size());
                try {
                    increaseWaitingTime();
                    matchPlayersOneVsOne();
                    matchPlayersRoyale();
//                    //如果当前池子里有人，则加一个虚拟的
//                    if (oneToOnePlayers.size() > 0) {
//                        count1++;
//                        if (count1 == 10) {
//                            count1 = 0;
//                            addPlayer2(-1, 1000);
//                        }
//                    } else {
//                        count1 = 0;
//                    }
//                    if (royalePlayers.size() > 0) {
//                        count2++;
//                        if (count2 == 5) {
//                            count2 = 0;
//                            addPlayer2(-1, 1000);
//                        }
//                    } else {
//                        count2 = 0;
//                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    //获取1v1匹配
    public int getCurrentMatch() {
        return oneToOnePlayers.size();
    }

    // 将所有当前玩家的等待时间加1
    private void increaseWaitingTime() {
        for (Player player : oneToOnePlayers) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
        for (Player player : royalePlayers) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    // 尝试匹配所有玩家
    // 等待时间长的玩家优先进行匹配
    // 由于存储所有玩家的是list集合，因此下标小的等待时间一定更长
    private void matchPlayersOneVsOne() throws IOException {
        ojService.updateProblems();
        boolean[] used = new boolean[oneToOnePlayers.size()]; // 当前玩家是否匹配

        Boolean allFake = true;
        for (int i = 0; i < oneToOnePlayers.size(); i++) {
            if (oneToOnePlayers.get(i).getUserId()!=-1){
                allFake=false;
                break;
            }
        }
        if (allFake){
            oneToOnePlayers.clear();
            return;
        }

        for (int i = 0; i < oneToOnePlayers.size(); i++) {
            if (used[i]) continue;
            for (int j = i + 1; j < oneToOnePlayers.size(); j++) {
                if (used[j]) continue;
                Player a = oneToOnePlayers.get(i), b = oneToOnePlayers.get(j);
                if (checkMatched(a, b) && !a.getUserId().equals(b.getUserId())) {
                    used[i] = used[j] = true;
                    Random random = new Random();
                    Object[] values = ojService.problems.keySet().toArray();
                    int randomIndex = random.nextInt(values.length);
                    int randomQuestionId1 = (int) values[randomIndex];
                    ArrayList<Integer> questionIds = new ArrayList<>();
                    questionIds.add(randomQuestionId1);

                    ArrayList<Player> players = new ArrayList<>();
                    players.add(a);
                    players.add(b);

                    sendResult(players, questionIds); // 匹配成功之后返回结果
                    break;
                }
            }
        }

        // 匹配成功的需要从匹配池中剔除
        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < oneToOnePlayers.size(); i++) {
            if (!used[i]) {
                newPlayers.add(oneToOnePlayers.get(i));
            }
        }
        oneToOnePlayers = newPlayers;
    }

    //匹配大逃杀模式
    private void matchPlayersRoyale() throws IOException {
        ojService.updateProblems();

        Boolean allFake = true;
        for (int i = 0; i < royalePlayers.size(); i++) {
            if (royalePlayers.get(i).getUserId()!=-1){
                allFake=false;
                break;
            }
        }
        if (allFake){
            royalePlayers.clear();
            return;
        }

        //排序
        Collections.sort(royalePlayers, Collections.reverseOrder(playerComparator));
        while (royalePlayers.size() >= 8) {
            Collections.sort(royalePlayers, Collections.reverseOrder(playerComparator));
            List<Player> first8Players = royalePlayers.subList(0, 8);
            //用户
            ArrayList<Player> players = new ArrayList<>();
            //题目
            ArrayList<Integer> questionIds = new ArrayList<>();
            //随机
            Random random = new Random();
            Object[] values = ojService.problems.values().toArray();
            for (int i = 0; i < 5; i++) {
                int randomIndex = random.nextInt(values.length);
                int randomQuestionId = (int) values[randomIndex];
                //不允许同样的题目
                while (questionIds.contains(randomQuestionId)) {
                    randomIndex = random.nextInt(values.length);
                    randomQuestionId = (int) values[randomIndex];
                }
                questionIds.add(randomQuestionId);
            }
            for (Player player : first8Players) {
                players.add(player);
            }
            //匹配完成
            sendResult(players, questionIds);
            //清除
            royalePlayers.subList(0, 8).clear();
        }
    }

    // 判断两名玩家是否匹配
    // 根据战力差<=等待时间差*10进行匹配
    private boolean checkMatched(Player a, Player b) {
        int ratingDelta = Math.abs(a.getRating() - b.getRating()); // 战力差距
        int ratingTime = Math.min(a.getWaitingTime(), b.getWaitingTime()); // 最小等待时间
        return true;
    }

    // 返回匹配结果
    private void sendResult(ArrayList<Player> players, ArrayList<Integer> questionIds) throws IOException {
        System.out.println("send result: " + players.get(0).getUserId() + " " + players.get(1).getUserId());
        int num = players.size();

        // Game加入数据库
        Game game = new Game();
        if(num==2){ //单人模式
            game.setGameType(1);
        }else{ //大逃杀模式
            game.setGameType(2);
        }

        gameService.addGame(game);

        // UserGameRecord加入数据库
        for (int i = 0; i < num; i++) {
            UserGameRecord userGameRecord = new UserGameRecord();
            userGameRecord.setUserId(players.get(i).getUserId());
            //这个要改的有点多
            //userGameRecord.setQuestionId(questionIds);
            // TODO: userGameRecord.setQuestionId(questionIds.get(0));
            userGameRecord.setGameId(game.getId());
            userGameRecord.setTeam(i); //todo:多人修改逻辑
            gameService.save(userGameRecord);
        }

        // 返回
        Map<String, Integer> playerMap = new HashMap<>();
        for (int i = 0; i < num; i++) {
            playerMap.put(Integer.toString(i), players.get(i).getUserId());
        }


        //创建比赛
        CurrentGame currentGame = new CurrentGame();
        currentGame.setGameId(game.getId());
        currentGame.setQuestionId(questionIds);
        if(playerMap.size()==2){
            currentGame.setGameType(1);
        }else{
            currentGame.setGameType(2);
        }
        currentGame.setPlayerMap(playerMap);
        currentGame.setCurrentTime(LocalDateTime.now());
        Map<Integer, Integer> HPMAP = new HashMap<>();
        for (Integer eachUser : playerMap.values()) {
            HPMAP.put(eachUser, 100);
        }
        currentGame.setHPMAP(HPMAP);
        Map<Integer, Integer> acMAP = new HashMap<>();
        for (Integer eachUser : playerMap.values()) {
            acMAP.put(eachUser, 0);
        }
        currentGame.setAcMAP(acMAP);

        for (int i = 0; i < num; i++) {
            returnMatchResult(players.get(i).getUserId(), questionIds, playerMap, game.getId(),currentGame);
        }
    }

    public static void main(String[] args) throws IOException {
        MatchingPool matchingPool = new MatchingPool();
        matchingPool.matchPlayersOneVsOne();
    }
}