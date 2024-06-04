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
    public WebSocketServerTest() {
    
    }
    
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

// ...  等等
    
    /**
     * 创建 WebSocket 的客户端做测试
     *
     * @throws Exception
     */
    @Test
    public void gameOne() throws Exception { // 该局以玩家1的AC结束，具体指ANSWER_REFRESH请求
        MatchService.start(); // MatchService线程负责匹配1V1玩家
        MyWebSocketClient myWebSocketClient1 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 1); // client能和server通信，主要通过这个url，能找到彼此
        MyWebSocketClient myWebSocketClient2 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 2);
        myWebSocketClient1.connect();
        myWebSocketClient2.connect();
        while (!myWebSocketClient1.getReadyState().equals(ReadyState.OPEN)) {
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
        Thread.sleep(3000); // 等待MATCH_START
        
        JSONObject output_CHAT_REQ = new JSONObject();
        JSONObject dataOutput_CHAT_REQ = new JSONObject();
        dataOutput_CHAT_REQ.put("type", "global"); // MATCH_START后才可以发送该类型请求
        dataOutput_CHAT_REQ.put("message", "菜就多练"); // 发送者自己也能收到该消息
        output_CHAT_REQ.put("type", "CHAT_REQ");
        output_CHAT_REQ.put("data", dataOutput_CHAT_REQ);
        output_CHAT_REQ.put("id", "1");
        
        JSONObject output_CHAT_REQ2 = new JSONObject();
        JSONObject dataOutput_CHAT_REQ2 = new JSONObject();
        dataOutput_CHAT_REQ2.put("type", "team"); // MATCH_START后才可以发送该类型请求
        dataOutput_CHAT_REQ2.put("message", "不收徒");
        output_CHAT_REQ2.put("type", "CHAT_REQ");
        output_CHAT_REQ2.put("data", dataOutput_CHAT_REQ2);
        output_CHAT_REQ2.put("id", "1");
        myWebSocketClient1.send(output_CHAT_REQ.toJSONString());
        myWebSocketClient1.send(output_CHAT_REQ2.toJSONString());
        
        JSONObject output_POS_UPDATE = new JSONObject();
        JSONObject dataOutput_POS_UPDATE = new JSONObject();
        dataOutput_POS_UPDATE.put("row", "2");
        dataOutput_POS_UPDATE.put("col", "2");
        dataOutput_POS_UPDATE.put("total_rows", "3");
        output_POS_UPDATE.put("type", "POS_UPDATE");
        output_POS_UPDATE.put("data", dataOutput_POS_UPDATE);
        output_POS_UPDATE.put("id", "1");
        myWebSocketClient1.send(output_POS_UPDATE.toJSONString());
        Thread.sleep(1000);
        
        JSONObject output_ANSWER_REFRESH = new JSONObject();
        JSONObject dataOutput_ANSWER_REFRESH = new JSONObject();
        dataOutput_ANSWER_REFRESH.put("submit_id", "5cb27cbb60bdcf0ab6a4ddad2412d4e7"); // MATCH_START后才可以发送该类型请求
        output_ANSWER_REFRESH.put("type", "ANSWER_REFRESH");
        output_ANSWER_REFRESH.put("data", dataOutput_ANSWER_REFRESH);
        output_ANSWER_REFRESH.put("id", "1");
        myWebSocketClient1.send(output_ANSWER_REFRESH.toJSONString());
        
        
        Thread.sleep(5000); // 需要等待 游戏匹配需要时间
        
    }
    
    @Test
    public void gameTwo() throws Exception { // 该局以玩家3的投降结束，具体指SURRENDER请求
        MatchService.start(); // MatchService线程负责匹配1V1玩家
        MyWebSocketClient myWebSocketClient1 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 3); // client能和server通信，主要通过这个url，能找到彼此
        MyWebSocketClient myWebSocketClient2 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 4);
        myWebSocketClient1.connect();
        myWebSocketClient2.connect();
        while (!myWebSocketClient1.getReadyState().equals(ReadyState.OPEN)) {
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
        Thread.sleep(3000); // 等待MATCH_START
    
        JSONObject output_ITEM_SEND = new JSONObject();
        JSONObject dataOutput_ITEM_SEND = new JSONObject();
        dataOutput_ITEM_SEND.put("type", "tomato");
        output_ITEM_SEND.put("type", "ITEM_SEND");
        output_ITEM_SEND.put("data", dataOutput_ITEM_SEND);
        output_ITEM_SEND.put("id", "1");
        myWebSocketClient1.send(output_ITEM_SEND.toJSONString());
    
        JSONObject output_ITEM_SEND2 = new JSONObject();
        JSONObject dataOutput_ITEM_SEND2 = new JSONObject();
        dataOutput_ITEM_SEND2.put("type", "tomato3");
        output_ITEM_SEND2.put("type", "ITEM_SEND");
        output_ITEM_SEND2.put("data", dataOutput_ITEM_SEND2);
        output_ITEM_SEND2.put("id", "1");
        myWebSocketClient2.send(output_ITEM_SEND2.toJSONString());
        Thread.sleep(1000);
    
        JSONObject output_SURRENDER = new JSONObject();
        JSONObject dataOutput_SURRENDER = new JSONObject();
        output_SURRENDER.put("type", "SURRENDER");
        output_SURRENDER.put("data", dataOutput_SURRENDER);
        output_SURRENDER.put("id", "1");
        myWebSocketClient1.send(output_SURRENDER.toJSONString());
        Thread.sleep(5000);
    }
    
    @Test
    public void roomTest() throws Exception {
        MatchService.start(); // MatchService线程负责匹配1V1玩家
        MyWebSocketClient myWebSocketClient1 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 5); // client能和server通信，主要通过这个url，能找到彼此
        myWebSocketClient1.connect();
        while (!myWebSocketClient1.getReadyState().equals(ReadyState.OPEN)) {
            log.info("WebSocket客户端连接中，请稍等...");
            Thread.sleep(500);
        }
    
        JSONObject output_LOGIN_REQ = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ = new JSONObject();
        dataOutput_LOGIN_REQ.put("token", 1);
        output_LOGIN_REQ.put("type", "LOGIN_REQ");
        output_LOGIN_REQ.put("data", dataOutput_LOGIN_REQ);
        output_LOGIN_REQ.put("id", "1");
    
        myWebSocketClient1.send(output_LOGIN_REQ.toJSONString());
        Thread.sleep(500); // 需要等待server端给client发回信息，不能让client提前结束
        
        JSONObject output_ROOM_REQUEST = new JSONObject();
        JSONObject dataOutput_ROOM_REQUEST = new JSONObject();
        dataOutput_ROOM_REQUEST.put("roomid", 15);
        dataOutput_ROOM_REQUEST.put("type", "in");
        output_ROOM_REQUEST.put("type", "ROOM_REQUEST");
        output_ROOM_REQUEST.put("data", dataOutput_ROOM_REQUEST);
        output_ROOM_REQUEST.put("id", "1");
        myWebSocketClient1.send(output_ROOM_REQUEST.toJSONString());
        Thread.sleep(500);
    
        JSONObject output_ROOM_REQUEST2 = new JSONObject();
        JSONObject dataOutput_ROOM_REQUEST2 = new JSONObject();
        dataOutput_ROOM_REQUEST2.put("roomid", 15);
        dataOutput_ROOM_REQUEST2.put("type", "out");
        output_ROOM_REQUEST2.put("type", "ROOM_REQUEST");
        output_ROOM_REQUEST2.put("data", dataOutput_ROOM_REQUEST2);
        output_ROOM_REQUEST2.put("id", "1");
        myWebSocketClient1.send(output_ROOM_REQUEST2.toJSONString());
        Thread.sleep(500);
        
        Thread.sleep(5000);
    }
}