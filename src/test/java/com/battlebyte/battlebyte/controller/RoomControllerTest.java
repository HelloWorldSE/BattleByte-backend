package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.dao.RoomDao;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.Room;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.service.RoomService;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.RsaUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class RoomControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    RoomDao roomDao;
    
    @Autowired
    private RoomService roomService;
    
    @Transactional
    @Rollback()
    @Test
    void addRoom() throws Exception { // bug:room的game_id为null，没有设置值，并未实现游戏和房间的联系
        Room room = new Room();
        room.setName("祖安房");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(room);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/room/add")
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
    void updateRoom() throws Exception {
        Room room = new Room();
        room.setName("6系");
        room.setId(4); // 指定对1号用户的房间(房间号4号)的房间名改为6系
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(room);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/room/update")
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
    void findRoom() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/room")
                .param("status", "0")
                .param("page", "1")
                .param("pageSize", "5")
                .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
        );
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
    }

    @Transactional
    @Rollback()
    @Test
    void findRoomById() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/room")
                .param("id", "4")
                .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3MjA4MjMsInVzZXJJZCI6MX0.rC0g8WEjYYWOr1pquxRg6tx5sXwZmo-v4091f_Ci-kU")
        );
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
    }
}