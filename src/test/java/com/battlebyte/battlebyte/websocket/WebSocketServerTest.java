package com.battlebyte.battlebyte.websocket;

import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.service.MatchService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;
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
//        MatchService.start(); // MatchService线程负责匹配1V1玩家
        // 匹配池对本类所有函数只需要开一个
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

    @Transactional
    @Test
    @Rollback()
    public void roomTest() throws Exception { // 经测试，room_request的out无法移除房间内用户
        MyWebSocketClient myWebSocketClient2 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 2); // client能和server通信，主要通过这个url，能找到彼此
        myWebSocketClient2.connect();

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

        JSONObject output_LOGIN_REQ2 = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ2 = new JSONObject();
        dataOutput_LOGIN_REQ2.put("token", 2);  // 2号id在之前的测试中已存在多个房间内，包括本函数测试的15号房间，所以不需要发room_request也在房间内
        output_LOGIN_REQ2.put("type", "LOGIN_REQ");
        output_LOGIN_REQ2.put("data", dataOutput_LOGIN_REQ2);
        output_LOGIN_REQ2.put("id", "1");

        myWebSocketClient2.send(output_LOGIN_REQ2.toJSONString());
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

    @Transactional
    @Test
    @Rollback()
    public void roomStartTest() throws InterruptedException, URISyntaxException {
        // 经测试,room_request类型请求你不能多个客户端同时send，否则只能收到1条room_refresh消息，而不是每个客户端收到一条
        MyWebSocketClient myWebSocketClient1 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 5); // client能和server通信，主要通过这个url，能找到彼此
        MyWebSocketClient myWebSocketClient2 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 6);
        MyWebSocketClient myWebSocketClient3 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 7);
        MyWebSocketClient myWebSocketClient4 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 8);
        MyWebSocketClient myWebSocketClient5 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 9);
        MyWebSocketClient myWebSocketClient6 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 10);
        MyWebSocketClient myWebSocketClient7 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 11);
        MyWebSocketClient myWebSocketClient8 = new MyWebSocketClient(new URI("ws://localhost:9090/server"), 12);
        myWebSocketClient1.connect();
        myWebSocketClient2.connect();
        myWebSocketClient3.connect();
        myWebSocketClient4.connect();
        myWebSocketClient5.connect();
        myWebSocketClient6.connect();
        myWebSocketClient7.connect();
        myWebSocketClient8.connect();
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

        JSONObject output_LOGIN_REQ3 = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ3 = new JSONObject();
        dataOutput_LOGIN_REQ3.put("token", 3);
        output_LOGIN_REQ3.put("type", "LOGIN_REQ");
        output_LOGIN_REQ3.put("data", dataOutput_LOGIN_REQ3);
        output_LOGIN_REQ3.put("id", "1");

        JSONObject output_LOGIN_REQ4 = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ4 = new JSONObject();
        dataOutput_LOGIN_REQ4.put("token", 4);
        output_LOGIN_REQ4.put("type", "LOGIN_REQ");
        output_LOGIN_REQ4.put("data", dataOutput_LOGIN_REQ4);
        output_LOGIN_REQ4.put("id", "1");

        JSONObject output_LOGIN_REQ5 = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ5 = new JSONObject();
        dataOutput_LOGIN_REQ5.put("token", 5);
        output_LOGIN_REQ5.put("type", "LOGIN_REQ");
        output_LOGIN_REQ5.put("data", dataOutput_LOGIN_REQ5);
        output_LOGIN_REQ5.put("id", "1");

        JSONObject output_LOGIN_REQ6 = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ6 = new JSONObject();
        dataOutput_LOGIN_REQ6.put("token", 6);
        output_LOGIN_REQ6.put("type", "LOGIN_REQ");
        output_LOGIN_REQ6.put("data", dataOutput_LOGIN_REQ6);
        output_LOGIN_REQ6.put("id", "1");

        JSONObject output_LOGIN_REQ7 = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ7 = new JSONObject();
        dataOutput_LOGIN_REQ7.put("token", 7);
        output_LOGIN_REQ7.put("type", "LOGIN_REQ");
        output_LOGIN_REQ7.put("data", dataOutput_LOGIN_REQ7);
        output_LOGIN_REQ7.put("id", "1");

        JSONObject output_LOGIN_REQ8 = new JSONObject();
        JSONObject dataOutput_LOGIN_REQ8 = new JSONObject();
        dataOutput_LOGIN_REQ8.put("token", 8);
        output_LOGIN_REQ8.put("type", "LOGIN_REQ");
        output_LOGIN_REQ8.put("data", dataOutput_LOGIN_REQ8);
        output_LOGIN_REQ8.put("id", "1");

        myWebSocketClient1.send(output_LOGIN_REQ.toJSONString());
        Thread.sleep(500); // 需要等待server端给client发回信息，不能让client提前结束
        myWebSocketClient2.send(output_LOGIN_REQ2.toJSONString());
        Thread.sleep(500);
        myWebSocketClient3.send(output_LOGIN_REQ3.toJSONString());
        Thread.sleep(500);
        myWebSocketClient4.send(output_LOGIN_REQ4.toJSONString());
        Thread.sleep(500);
        myWebSocketClient5.send(output_LOGIN_REQ5.toJSONString());
        Thread.sleep(500);
        myWebSocketClient6.send(output_LOGIN_REQ6.toJSONString());
        Thread.sleep(500);
        myWebSocketClient7.send(output_LOGIN_REQ7.toJSONString());
        Thread.sleep(500);
        myWebSocketClient8.send(output_LOGIN_REQ8.toJSONString());
        Thread.sleep(500);

        JSONObject output_ROOM_REQUEST_OUT = new JSONObject();
        JSONObject dataOutput_ROOM_REQUEST_OUT = new JSONObject();
        dataOutput_ROOM_REQUEST_OUT.put("roomid", 30);
        dataOutput_ROOM_REQUEST_OUT.put("type", "out");
        output_ROOM_REQUEST_OUT.put("type", "ROOM_REQUEST");
        output_ROOM_REQUEST_OUT.put("data", dataOutput_ROOM_REQUEST_OUT);
        output_ROOM_REQUEST_OUT.put("id", "1");
        myWebSocketClient1.send(output_ROOM_REQUEST_OUT.toJSONString());
        Thread.sleep(500);
        myWebSocketClient2.send(output_ROOM_REQUEST_OUT.toJSONString());
        Thread.sleep(500);
        myWebSocketClient3.send(output_ROOM_REQUEST_OUT.toJSONString());
        Thread.sleep(500);
        myWebSocketClient4.send(output_ROOM_REQUEST_OUT.toJSONString());
        Thread.sleep(500);
        myWebSocketClient5.send(output_ROOM_REQUEST_OUT.toJSONString());
        Thread.sleep(500);
        myWebSocketClient6.send(output_ROOM_REQUEST_OUT.toJSONString());
        Thread.sleep(500);
        myWebSocketClient7.send(output_ROOM_REQUEST_OUT.toJSONString());
        Thread.sleep(500);
        myWebSocketClient8.send(output_ROOM_REQUEST_OUT.toJSONString());
        Thread.sleep(500);

        JSONObject output_ROOM_REQUEST = new JSONObject();
        JSONObject dataOutput_ROOM_REQUEST = new JSONObject();
        dataOutput_ROOM_REQUEST.put("roomid", 30);
        dataOutput_ROOM_REQUEST.put("type", "in");
        output_ROOM_REQUEST.put("type", "ROOM_REQUEST");
        output_ROOM_REQUEST.put("data", dataOutput_ROOM_REQUEST);
        output_ROOM_REQUEST.put("id", "1");
        myWebSocketClient1.send(output_ROOM_REQUEST.toJSONString());
        Thread.sleep(500);
        myWebSocketClient2.send(output_ROOM_REQUEST.toJSONString());
        Thread.sleep(500);
        myWebSocketClient3.send(output_ROOM_REQUEST.toJSONString());
        Thread.sleep(500);
        myWebSocketClient4.send(output_ROOM_REQUEST.toJSONString());
        Thread.sleep(500);
        myWebSocketClient5.send(output_ROOM_REQUEST.toJSONString());
        Thread.sleep(500);
        myWebSocketClient6.send(output_ROOM_REQUEST.toJSONString());
        Thread.sleep(500);
        myWebSocketClient7.send(output_ROOM_REQUEST.toJSONString());
        Thread.sleep(500);
        myWebSocketClient8.send(output_ROOM_REQUEST.toJSONString());
        Thread.sleep(500);

        JSONObject output_ROOM_GET_INFO = new JSONObject();
        JSONObject dataOutput_ROOM_GET_INFO = new JSONObject();
        dataOutput_ROOM_GET_INFO.put("roomid", 30);
        output_ROOM_GET_INFO.put("type", "ROOM_GET_INFO");
        output_ROOM_GET_INFO.put("data", dataOutput_ROOM_GET_INFO);
        output_ROOM_GET_INFO.put("id", "1");
        myWebSocketClient1.send(output_ROOM_GET_INFO.toJSONString());
        Thread.sleep(1000);

        JSONObject output_ROOM_START = new JSONObject();
        JSONObject dataOutput_ROOM_START = new JSONObject();
        dataOutput_ROOM_START.put("roomid", 30);
        output_ROOM_START.put("type", "ROOM_START"); // 暂时有问题，无法测试
        output_ROOM_START.put("data", dataOutput_ROOM_START);
        output_ROOM_START.put("id", "1");
        myWebSocketClient1.send(output_ROOM_START.toJSONString());
        Thread.sleep(5000);
    }
}