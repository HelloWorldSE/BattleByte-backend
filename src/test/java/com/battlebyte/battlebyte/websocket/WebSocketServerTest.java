package com.battlebyte.battlebyte.websocket;

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
        MyWebSocketClient myWebSocketClient = new MyWebSocketClient(new URI("ws://localhost:9090/server"));
        myWebSocketClient.connect();
        while (!myWebSocketClient.getReadyState().equals(ReadyState.OPEN)){
            log.info("WebSocket客户端连接中，请稍等...");
            Thread.sleep(500);
        }
        myWebSocketClient.send("{\n" +
                "    \"type\": \"LOGIN_REQ\",\n" +
                "    \"data\": {\n" +
                "        \"token\": \"1\"\n" +
                "    },\n" +
                "    \"id\": 1\n" +
                "}");
        Thread.sleep(500);

    }
}