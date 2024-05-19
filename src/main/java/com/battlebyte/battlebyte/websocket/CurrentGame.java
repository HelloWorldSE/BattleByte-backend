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

    //队号，用户id
    Map<String, Integer> playerMap;

    //用户id，HP
    Map<Integer, Integer> HPMAP;

    int currentQuestion = 0;
}
