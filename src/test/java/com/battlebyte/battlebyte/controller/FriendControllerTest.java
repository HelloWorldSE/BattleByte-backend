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
    
    @MockBean
    private FriendService friendService;
    
    @Test
    void getFriend() {
        Page<FriendDTO> mockPage = Mockito.mock(Page.class);
        when(friendService.getFriend(any(Integer.class),any(String.class),any(Integer.class),any(Pageable.class)))
                .thenReturn(mockPage);
    }
    
    @Test
    void addFriend() throws Exception {
        int dest = 1;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dest);
        doNothing().when(friendService).addFriend(any(Integer.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/add-apply")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Test
    void getFriendApplications() throws Exception {
        Page<FriendDTO> mockPage = Mockito.mock(Page.class);
        when(friendService.getFriendApplications(any(Integer.class),any(String.class),any(Integer.class),any(Pageable.class)))
                .thenReturn(mockPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/update-record")
                        .param("id", "1")
                        .param("name", "newAdmin1")
                        .param("page", "1")
                        .param("pageSize", "5")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    
    @Test
    void process() throws Exception {
        doNothing().when(friendService).processApply(any(Integer.class),any(boolean.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/process")
                        .param("id","1")
                        .param("accept","true")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}