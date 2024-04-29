package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.service.match.MatchingPool;
import com.battlebyte.battlebyte.websocket.WebSocketServer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 匹配池
 */
@Service
public class MatchService {

    public final static MatchingPool matchingPool = new MatchingPool();

    public static void start() {
        matchingPool.start();
    }

    public static void addPlayer(Integer userId, Integer rating) {
        System.out.println("add player: " + userId + " " + rating);
        matchingPool.addPlayer(userId, rating, 0);
    }

    public static void removePlayer(Integer userId) {
        System.out.println("remove player: " + userId);
        matchingPool.removePlayer(userId);
    }

    public static void returnMatchResult(Integer userId, int questionId, Map<String,Integer> playerMap) throws IOException {
        WebSocketServer.return_MATCH_ENTER(userId, questionId, playerMap);
    }

}