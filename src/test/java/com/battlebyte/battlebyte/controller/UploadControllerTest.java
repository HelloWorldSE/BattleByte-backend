package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.service.OJService;
import com.battlebyte.battlebyte.service.UploadService;
import com.battlebyte.battlebyte.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class UploadControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    UserDao userDao;
    
    @MockBean
    private UploadService uploadService;
    
    @Transactional
    @Rollback()
    @Test
    void updateAvatar() throws Exception {
        String fileName = "user1Id - user1";
        when(uploadService.updateAvatar(any(MultipartFile.class))).thenReturn(fileName);
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", "text/plain", "test image content".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/avatar").file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}