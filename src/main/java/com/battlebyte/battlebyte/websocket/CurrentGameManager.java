package com.battlebyte.battlebyte.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CurrentGameManager {
    private WebSocketServer webSocketServer;
    @Autowired
    public CurrentGameManager(WebSocketServer webSocketServer){
        this.webSocketServer=webSocketServer;
    }
    @Scheduled(fixedRate = 2000)
    public void performTask() throws IOException {
        webSocketServer.manageGame();
    }
}
