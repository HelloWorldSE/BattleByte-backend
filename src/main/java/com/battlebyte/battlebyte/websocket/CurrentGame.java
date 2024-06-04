package com.battlebyte.battlebyte.websocket;

import cn.hutool.core.collection.LineIter;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
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

    Map<String, Integer> getHP(){
        Map<String, Integer> hpMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : HPMAP.entrySet()) {
            hpMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return hpMap;
    }

    Map<String, Integer> getAc(){
        Map<String, Integer> aMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : acMAP.entrySet()) {
            aMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return aMap;
    }

}