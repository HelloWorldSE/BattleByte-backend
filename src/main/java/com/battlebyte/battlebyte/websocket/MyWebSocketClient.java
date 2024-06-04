package com.battlebyte.battlebyte.websocket;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.enums.ReadyState;

import java.net.URI;

@Slf4j
public class MyWebSocketClient extends WebSocketClient {
    public MyWebSocketClient(URI uri) {
        super(uri);
    }
    
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("客户端连接成功");
    }
    
    @Override
    public void onMessage(String message) {
        log.info("客户端接收到消息：" + message);
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("客户端关闭成功");
    }
    
    @Override
    public void onError(Exception ex) {
        log.error("客户端出错", ex);
    }
}
