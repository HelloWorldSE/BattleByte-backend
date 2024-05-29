package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.service.match.MatchingPool;
import com.battlebyte.battlebyte.websocket.CurrentGame;
import com.battlebyte.battlebyte.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 匹配池
 */
@Service

public class MatchService {

    public static MatchingPool matchingPool;

    public MatchService(MatchingPool matchingPool){
        this.matchingPool=matchingPool;
    }

    public static void start() {
        matchingPool.start();
    }

    public static void addPlayer1(Integer userId, Integer rating) {
        System.out.println("add player 1v1: " + userId + " " + rating);
        matchingPool.addPlayer1(userId, rating);
    }

    public static void addPlayer2(Integer userId, Integer rating) {
        System.out.println("add player 大逃杀: " + userId + " " + rating);
        matchingPool.addPlayer2(userId, rating);
    }

    public static void removePlayer(Integer userId) {
        System.out.println("remove player: " + userId);
        matchingPool.removePlayer(userId);
    }

    public static void returnMatchResult(Integer userId, ArrayList<Integer> questionId, Map<String, Integer> playerMap, int gameId, CurrentGame currentGame) throws IOException {
        WebSocketServer.return_MATCH_ENTER(userId, questionId, playerMap, gameId,currentGame);
    }

}