package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.config.UserToken;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.battlebyte.battlebyte.entity.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class UserServiceTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    UserDao userDao;
    
    @MockBean
    private UserService userService;
    //@MockBean
    //private User user;
    
    @Transactional
    @Rollback()
    @Test
    void register() throws Exception {
        User user = new User();
        user.setUserName("cbw");
        user.setPassword("123");
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
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void update() throws Exception {
        User user1 = new User();
        user1.setId(1);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user1);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/update")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void login() throws Exception {
        String token = JwtUtil.createToken(1, "123");
        UserToken userToken = new UserToken(token);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .header("Authorization",userToken)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
}