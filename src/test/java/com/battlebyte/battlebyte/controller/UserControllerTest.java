package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.config.UserToken;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.entity.dto.LoginDTO;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import com.battlebyte.battlebyte.service.OJService;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.JwtUtil;
import com.battlebyte.battlebyte.util.RsaUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    UserDao userDao;
    
    @Autowired
    private UserService userService;
    
    @Transactional
    @Rollback()
    @Test
    void registerUser() throws Exception {
        User user = new User();
        user.setUserName("cbw");
        user.setPassword(RsaUtils.encrypt("123123"));
        user.setUserEmail("123@buaa.com");
        user.setAvatar("1");
        user.setRating(5);
        user.setSign("male");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTYzNDcwOTIsInVzZXJJZCI6MX0.Bd2-YnlM8TdKcRMOMoo2IDCYhqsgoC-pZU73stZPRAY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void loginUser() throws Exception {
        String password = RsaUtils.encrypt("123456");
        String requestBody = "{\"userName\": \"user1\", \"password\": \"" + password + "\"}";
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTYzNDcwOTIsInVzZXJJZCI6MX0.Bd2-YnlM8TdKcRMOMoo2IDCYhqsgoC-pZU73stZPRAY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void update() throws Exception {
        User user1 = new User();
        //user1.setId(9);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user1);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user/update")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTYzNDcwOTIsInVzZXJJZCI6MX0.Bd2-YnlM8TdKcRMOMoo2IDCYhqsgoC-pZU73stZPRAY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void getUser() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user")
                .param("id", "1")
                .param("name", "userAdmin1")
                .param("page", "1")
                .param("pageSize", "5")
                .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTYzNDcwOTIsInVzZXJJZCI6MX0.Bd2-YnlM8TdKcRMOMoo2IDCYhqsgoC-pZU73stZPRAY")
        );
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void getById() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/profile")
                .param("id", "1")
                .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTYzNDcwOTIsInVzZXJJZCI6MX0.Bd2-YnlM8TdKcRMOMoo2IDCYhqsgoC-pZU73stZPRAY")
        );
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
    }
}