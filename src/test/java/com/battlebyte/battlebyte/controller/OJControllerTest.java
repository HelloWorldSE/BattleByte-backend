package com.battlebyte.battlebyte.controller;

import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.GameService;
import com.battlebyte.battlebyte.service.OJService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class OJControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    UserDao userDao;
    
    @Autowired
    private OJService ojService;
    
    @Transactional
    @Rollback()
    @Test
    void get() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/oj/problem")
                .param("id","1")
                .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTYzNDcwOTIsInVzZXJJZCI6MX0.Bd2-YnlM8TdKcRMOMoo2IDCYhqsgoC-pZU73stZPRAY")
        );
        resultActions.andReturn().getResponse().setCharacterEncoding("UTF-8");
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());
    }
    
    private static JSONObject getString(String url, HttpEntity<String> requestEntity) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JSONObject.class);
        HttpStatusCode statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new ServiceException("访问失败");
        }
    }
    
    // 传输信息字段存在一定问题
    @Transactional
    @Rollback()
    @Test
    void submit() throws Exception {
        String requestBody = "{\"problem_id\": 1, \"language\": \"C\", \"code\": \"int main() {return 0;}\"}";
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/oj/submit")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTYzNDcwOTIsInVzZXJJZCI6MX0.Bd2-YnlM8TdKcRMOMoo2IDCYhqsgoC-pZU73stZPRAY")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}