package com.battlebyte.battlebyte.websocket;

import lombok.Data;

import java.util.Map;
import java.time.LocalDateTime;
@Data
public class CurrentGame {
    int gameId;
    int questionId;
    LocalDateTime currentTime;
    Map<String, Integer> playerMap;
}
