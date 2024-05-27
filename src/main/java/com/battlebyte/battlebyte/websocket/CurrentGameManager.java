package com.battlebyte.battlebyte.websocket;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CurrentGameManager {
    WebSocketServer webSocketServer = new WebSocketServer();
    @Scheduled(fixedRate = 2000)
    public void performTask() throws IOException {
        webSocketServer.manageGame();
    }
}
