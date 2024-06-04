package com.battlebyte.battlebyte.websocket;

import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Slf4j
public class WebSocketServerTest {
    public WebSocketServerTest(){
    
    }
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Before
    public void before(){
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

// ...  等等
    
    /**
     * 创建 WebSocket 的客户端做测试
     * @throws Exception
     */
    @Test
    public void websocketClient() throws Exception{
        MatchService.start(); // MatchService线程负责匹配1V1玩家
        MyWebSocketClient myWebSocketClient1 = new MyWebSocketClient(new URI("ws://localhost:9090/server")); // client能和server通信，主要通过这个url，能找到彼此
        MyWebSocketClient myWebSocketClient2 = new MyWebSocketClient(new URI("ws://localhost:9090/server"));
        myWebSocketClient1.connect();
        myWebSocketClient2.connect();
        while (!myWebSocketClient1.getReadyState().equals(ReadyState.OPEN)){
            log.info("WebSocket客户端连接中，请稍等...");
            Thread.sleep(500);
        }
    
        JSONObject output_LOGIN_REQ = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ = new JSONObject();
        dataOutput_LOGIN_REQ.put("token", 1);
        output_LOGIN_REQ.put("type", "LOGIN_REQ");
        output_LOGIN_REQ.put("data", dataOutput_LOGIN_REQ);
        output_LOGIN_REQ.put("id", "1");
    
        JSONObject output_LOGIN_REQ2 = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ2 = new JSONObject();
        dataOutput_LOGIN_REQ2.put("token", 2);
        output_LOGIN_REQ2.put("type", "LOGIN_REQ");
        output_LOGIN_REQ2.put("data", dataOutput_LOGIN_REQ2);
        output_LOGIN_REQ2.put("id", "1");
    
        JSONObject output_MATCH_REQ = new JSONObject();
        JSONObject dataOutput_MATCH_REQ = new JSONObject();
        dataOutput_MATCH_REQ.put("type", 1);
        output_MATCH_REQ.put("type", "MATCH_REQ");
        output_MATCH_REQ.put("data", dataOutput_MATCH_REQ);
        output_MATCH_REQ.put("id", "1");
        
        myWebSocketClient1.send(output_LOGIN_REQ.toJSONString());
        Thread.sleep(500); // 需要等待server端给client发回信息，不能让client提前结束
        myWebSocketClient1.send(output_MATCH_REQ.toJSONString());

        myWebSocketClient2.send(output_LOGIN_REQ2.toJSONString());
        Thread.sleep(500);
        myWebSocketClient2.send(output_MATCH_REQ.toJSONString());
        Thread.sleep(10000);
        
    }
}