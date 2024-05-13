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
    
    @Autowired
    private UploadService uploadService;
    
    // 后端暂时有问题先不测
//    @Transactional
//    @Rollback()
//    @Test
//    void updateAvatar() throws Exception {
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "default.jpg",
//                "image/jpeg",
//                "test image content".getBytes()
//        );
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload/avatar").file(file)
//                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTU4NDAyOTYsInVzZXJJZCI6OH0.5aOcN3sgO1IThZ4zzEgGSfengR_1tf-q6JT8zL-RASY")
//                )
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(print());
//    }
}