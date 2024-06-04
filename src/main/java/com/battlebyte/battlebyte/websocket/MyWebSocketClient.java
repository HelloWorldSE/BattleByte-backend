package com.battlebyte.battlebyte.websocket;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.enums.ReadyState;

import java.net.URI;

@Slf4j
public class MyWebSocketClient extends WebSocketClient {
    private int uid;
    
    public MyWebSocketClient(URI uri, int uid) {
        super(uri);
        this.uid = uid;
    }
    
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("客户端" + uid + "连接成功");
    }
    
    @Override
    public void onMessage(String message) {
        log.info("客户端" + uid + "接收到消息：" + message);
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("客户端" + uid + "关闭成功");
    }
    
    @Override
    public void onError(Exception ex) {
        log.error("客户端" + uid + "出错", ex);
    }
}
