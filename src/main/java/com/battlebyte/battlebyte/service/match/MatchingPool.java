package com.battlebyte.battlebyte.service.match;

import com.battlebyte.battlebyte.service.match.Player;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.battlebyte.battlebyte.service.MatchService.returnMatchResult;

/**
 * 匹配池
 */
@Component
public class MatchingPool extends Thread {

    private static List<Player> players = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;

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
                    sendResult(a, b); // 匹配成功之后返回结果
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
    private void sendResult(Player a, Player b) throws IOException {
        System.out.println("send result: " + a.getUserId() + " " + b.getUserId());
        //todo:比赛信息加入数据库，返回
        returnMatchResult(a.getUserId());
        returnMatchResult(b.getUserId());
    }
}