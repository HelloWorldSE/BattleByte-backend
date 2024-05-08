package com.battlebyte.battlebyte.service.match;

import com.battlebyte.battlebyte.config.BeanContext;
import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.battlebyte.battlebyte.service.MatchService.returnMatchResult;

/**
 * 匹配池
 */
@Configurable
public class MatchingPool extends Thread {

    private GameService gameService;

    private static List<Player> players = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;

    public MatchingPool(){
        this.gameService= BeanContext.getApplicationContext().getBean(GameService.class);
    }
    /**
     * 向匹配池中添加一个玩家
     *
     * @param userId
     * @param rating
     */
    public void addPlayer(Integer userId, Integer botId, Integer rating) {
        lock.lock();
        try {
            players.add(new Player(userId, rating, 0));
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
            List<Player> newPlayers = new ArrayList<>();
            for (Player player : players) {
                if (!player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
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

                System.out.println("run once, current pool players num:" + players.size());
                try {
                    increaseWaitingTime();
                    matchPlayers();
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

    // 将所有当前玩家的等待时间加1
    private void increaseWaitingTime() {
        for (Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    // 尝试匹配所有玩家
    // 等待时间长的玩家优先进行匹配
    // 由于存储所有玩家的是list集合，因此下标小的等待时间一定更长
    private void matchPlayers() throws IOException {
        boolean[] used = new boolean[players.size()]; // 当前玩家是否匹配
        for (int i = 0; i < players.size(); i++) {
            if (used[i]) continue;
            for (int j = i + 1; j < players.size(); j++) {
                if (used[j]) continue;
                Player a = players.get(i), b = players.get(j);
                if (checkMatched(a, b) && !a.getUserId().equals(b.getUserId())) {
                    used[i] = used[j] = true;
                    Random random = new Random();
                    int randomQuestionId1 = random.nextInt(50) + 1;
//                    int randomQuestionId2 = random.nextInt(50) + 1;
                    ArrayList<Player> players = new ArrayList<>();
                    players.add(a);
                    players.add(b);

                    ArrayList<Integer> questionIds=new ArrayList<>();
                    questionIds.add(randomQuestionId1);
                    questionIds.add(randomQuestionId1);


                    sendResult(players, questionIds); // 匹配成功之后返回结果
                    break;
                }
            }
        }

        // 匹配成功的需要从匹配池中剔除
        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if (!used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;

    }

    // 判断两名玩家是否匹配
    // 根据战力差<=等待时间差*10进行匹配
    private boolean checkMatched(Player a, Player b) {
        int ratingDelta = Math.abs(a.getRating() - b.getRating()); // 战力差距
        int ratingTime = Math.min(a.getWaitingTime(), b.getWaitingTime()); // 最小等待时间
        return true;
    }

    // 返回匹配结果
    private void sendResult(ArrayList<Player>players , ArrayList<Integer>questionIds) throws IOException {
        System.out.println("send result: " + players.get(0).getUserId() + " " + players.get(1).getUserId());
        int num=2;

        // Game加入数据库
        Game game = new Game();
        game.setGameType(0);
        gameService.addGame(game);

        // UserGameRecord加入数据库
        for(int i=0;i<num;i++){
            UserGameRecord userGameRecord=new UserGameRecord();
            userGameRecord.setUserId(players.get(i).getUserId());
            userGameRecord.setQuestionId(questionIds.get(i));
            userGameRecord.setGameId(game.getId());
            userGameRecord.setTeam(i); //todo:多人修改逻辑
            gameService.save(userGameRecord);
        }

        // 返回
        Map<String, Integer> playerMap = new HashMap<>();
        for (int i=0;i<num;i++){
            playerMap.put(Integer.toString(i),players.get(i).getUserId());
        }
        for(int i=0;i<num;i++){
            returnMatchResult(players.get(i).getUserId(),questionIds.get(i),playerMap,game.getId());
        }
    }
}