package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.dao.GameDao;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import com.battlebyte.battlebyte.service.GameService;
import com.battlebyte.battlebyte.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    GameDao gameDao;
    
    @Autowired
    private GameService gameService;
    
    @Transactional
    @Rollback()
    @Test
    void addGame() throws Exception {
        Game game = new Game();
        game.setId(1);
        game.setGameType(1);
        game.setDate(new Date());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(game);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/game/add")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void updateGame() throws Exception {
        Game game = new Game();
        game.setId(1);
        game.setGameType(1);
        game.setDate(new Date());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(game);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/game/update")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void getPlayer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/game/player")
                        .param("id", "1")
                        .param("page", "1")
                        .param("pageSize", "10")
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void getGame() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/game")
                .param("id","1")
                .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
        );
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void save() throws Exception {
        UserGameRecord userGameRecord = new UserGameRecord();
        userGameRecord.setGameId(1);
        userGameRecord.setUserId(1);
        userGameRecord.setQuestionId(1);
        userGameRecord.setTeam(1);
        userGameRecord.setRank(1);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userGameRecord);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/game/update-record")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}