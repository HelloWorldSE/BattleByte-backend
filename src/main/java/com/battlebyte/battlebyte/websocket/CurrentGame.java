package com.battlebyte.battlebyte.websocket;

import cn.hutool.core.collection.LineIter;
import lombok.Data;

import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDateTime;

@Data
public class CurrentGame {
    int gameId;
    ArrayList<Integer> questionId;
    LocalDateTime currentTime;
    private Integer gameType; //1是单人，2是大逃杀

    //队号，用户id
    Map<String, Integer> playerMap;

    //用户id，HP
    Map<Integer, Integer> HPMAP;

    //用户id，ac的题目数
    Map<Integer, Integer> acMAP;

    int currentQuestion = 0;
}