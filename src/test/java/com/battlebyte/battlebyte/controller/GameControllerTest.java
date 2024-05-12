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
    
    @MockBean
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
        doNothing().when(gameService).addGame(any(Game.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/add")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
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
        doNothing().when(gameService).updateGame(any(Game.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/update")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void getPlayer() throws Exception {
        Page<UserGameDTO> mockPage = Mockito.mock(Page.class);
        when(gameService.getPlayer(any(Integer.class), any(Pageable.class))).thenReturn(mockPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/player")
                        .param("id", "1")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Test
    void save() throws Exception {
        doNothing().when(gameService).save(any(UserGameRecord.class));
        mockMvc.perform(MockMvcRequestBuilders.get("/update-record")
                        .param("gameId", "1")
                        .param("userId", "1")
                        .param("questionId", "1")
                        .param("team", "1")
                        .param("rank", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}