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
    
    @MockBean
    private OJService ojService;
    
    @Transactional
    @Rollback()
    @Test
    void get() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = getString("http://81.70.241.166:1233/api/problem?problem_id=1", new HttpEntity<>(headers));
        when(ojService.getProblem(any(Integer.class))).thenReturn(jsonObject);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/problem")
                .param("id","1")
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
    
    @Transactional
    @Rollback()
    @Test
    void submit() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = getString("http://81.70.241.166:1233/api/problem?problem_id=1", new HttpEntity<>(headers));
        when(ojService.submit(any(String.class))).thenReturn(jsonObject);
        String input = "int main(){return 0;}";
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(input);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/submit")
                        .content(json.getBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}