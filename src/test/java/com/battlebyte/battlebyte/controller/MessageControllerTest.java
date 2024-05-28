package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.dao.GameDao;
import com.battlebyte.battlebyte.service.FriendService;
import com.battlebyte.battlebyte.service.MessageService;
import com.battlebyte.battlebyte.util.RsaUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class MessageControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    GameDao gameDao;
    
    @Autowired
    private MessageService messageService;
    
    @Transactional
    @Rollback()
    @Test
    void send() throws Exception {
        String requestBody = "{\"id\": 4, \"content\": \"不收徒\"}";
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/message/send")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void broadcast() throws Exception {
        String requestBody = "菜就多练";
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/message/broadcast")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void receive() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/message")
                                .param("page", "1")
                                .param("pageSize", "5")
                                .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
                        // 传1号的token相当于查1号收到的消息，JwtUtil.getUserId()有token也可以正常运行
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    // 广播消息目前无法已读，因为message.getReceiver()=-1，message.getReceiver() == JwtUtil.getUserId()无法成立
    @Transactional
    @Rollback()
    @Test
    void read() throws Exception {
        int id = 2;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(id);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/message/read")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}