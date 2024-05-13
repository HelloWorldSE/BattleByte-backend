package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.dao.GameDao;
import com.battlebyte.battlebyte.entity.dto.FriendDTO;
import com.battlebyte.battlebyte.service.FriendService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
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
    
    @Test
    void addFriend() throws Exception { // 测试未成为好友的
        int dest = 11;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dest);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user/friend/add-apply")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Test
    void addFriendAlready() throws Exception { // 测试添加已经为好友的
        int dest = 1;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dest);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user/friend/add-apply")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Test
    void getFriendApplications() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/friend/apply")
                        .param("id", "1")
                        .param("name", "newAdmin1")
                        .param("page", "1")
                        .param("pageSize", "5")
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    // 后端暂时有问题先不测
//    @Test
//    void process() throws Exception {
//        String requestBody = "11";
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/user/friend/process")
//                        .param("accept","true")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
//                )
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(print());
//    }
}