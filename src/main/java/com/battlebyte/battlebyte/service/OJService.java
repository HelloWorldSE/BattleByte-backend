package com.battlebyte.battlebyte.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.exception.ServiceException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OJService {
    public String cookie="_pk_id.1.7ebb=d3c89c8c7f0158ca.1713614493.; " +
            "csrftoken=2YPsM0GU3sS9jsSAOSCFMuHqKNqblm0Ce6pjtZeErHtceTBXFS1poqm3fCPNNQMI; " +
            "sessionid=fmlxdqc9dj0abgqh8ycgq2cxz8hi2axl";
    public String X_Csrftoken="2YPsM0GU3sS9jsSAOSCFMuHqKNqblm0Ce6pjtZeErHtceTBXFS1poqm3fCPNNQMI";
    public String getProblem(Integer id) {
        String url = "http://81.70.241.166:1233/api/problem?problem_id=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", X_Csrftoken);
        headers.add("Cookie", cookie);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        return getString(url, requestEntity);
    }

    public String submit(String input)  {
        String url = "http://81.70.241.166:1233/api/submission";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", X_Csrftoken);
        headers.add("Cookie", cookie);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(input.replace(' ', ' '), headers);
        String result =  postString(url, requestEntity);

        JSONObject resultObj = JSON.parseObject(result);
        JSONObject data = resultObj.getJSONObject("data");
        String submission_id = data.getString("submission_id");
        return getResult(submission_id);
    }

    public String getResult(String input) {
        String url = "http://81.70.241.166:1233/api/submission?id=" + input;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", X_Csrftoken);
        headers.add("Cookie", cookie);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        return getString(url, requestEntity);
    }

    private static String getString(String url, HttpEntity<String> requestEntity) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        HttpStatusCode statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new ServiceException("访问失败");
        }
    }
    private static String postString(String url, HttpEntity<String> requestEntity) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        HttpStatusCode statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new ServiceException("访问失败");
        }
    }
}