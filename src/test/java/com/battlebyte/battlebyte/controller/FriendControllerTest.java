package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.dao.GameDao;
import com.battlebyte.battlebyte.entity.dto.FriendDTO;
import com.battlebyte.battlebyte.service.FriendService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class FriendControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    GameDao gameDao;
    
    @Autowired
    private FriendService friendService;
    
    @Transactional
    @Rollback()
    @Test
    void addFriend() throws Exception { // 测试未成为好友的
        int dest = 7;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dest);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user/friend/add-apply")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void addFriendAlready() throws Exception { // 测试添加已经为好友的
        int dest = 1;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dest);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user/friend/add-apply")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void getFriendApplications() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/friend/apply")
                        .param("id", "1")
                        .param("name", "newAdmin1")
                        .param("page", "1")
                        .param("pageSize", "5")
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback() // 使用数据库回滚可以不真实处理好友请求，否则这个函数只能测一次，因为请求被通过后就消失了
    // 请求被处理后由于回滚又被还原
    @Test
    void process() throws Exception {
        String requestBody = "2"; // 1号给3号的好友请求的id，请求的主码
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user/friend/process")
                        .param("accept", "true")
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
    void delFriend() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/user/friend/4")
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void getFriend() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/friend")
                        .param("page", "1")
                        .param("pageSize", "5")
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}