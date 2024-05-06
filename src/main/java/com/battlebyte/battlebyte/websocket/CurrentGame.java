package com.battlebyte.battlebyte.websocket;

import lombok.Data;

import java.util.Map;

@Data
public class CurrentGame {
    int gameId;
    int questionId;
    Map<String, Integer> playerMap;
}
