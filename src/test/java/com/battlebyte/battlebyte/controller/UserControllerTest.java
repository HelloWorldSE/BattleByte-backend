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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
    
    @MockBean
    private UserService userService;

    @Transactional
    @Rollback()
    @Test
    void registerUser() throws Exception {
        User user = new User();
        user.setUserName("cbw");
        user.setPassword("123");
        user.setUserEmail("123@buaa.com");
        user.setAvatar("1");
        user.setRating(5);
        user.setSign("male");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);
        doNothing().when(userService).register(any(User.class));
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
    void loginUser() throws Exception {
        LoginDTO loginDTO = Mockito.mock(LoginDTO.class);
        String requestBody = "{\"username\": \"cbw\", \"password\": \"123\"}";
        String token = JwtUtil.createToken(1, "123");
        UserToken userToken = new UserToken(token);
        when(userService.login(any(String.class), any(String.class))).thenReturn(loginDTO);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization",userToken)
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
        doNothing().when(userService).update(any(User.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user/update")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Transactional
    @Rollback()
    @Test
    void getUser() throws Exception {
        Page<UserInfoDTO> mockPage = Mockito.mock(Page.class);
        when(userService.getUser(any(Integer.class), any(String.class),any(Pageable.class))).thenReturn(mockPage);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user")
                        .param("id","1")
                        .param("name","userAdmin1")
                        .param("page","1")
                        .param("pageSize","5")
                );
                resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
                resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
    }
    
    @Transactional
    @Rollback()
    @Test
    void getById() throws Exception {
        UserProfileDTO userProfileDTO = Mockito.mock(UserProfileDTO.class);
        when(userService.findByUserId(any(Integer.class))).thenReturn(userProfileDTO);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/profile")
                .param("id", "1")
        );
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
    }
}