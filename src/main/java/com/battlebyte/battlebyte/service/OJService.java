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

//    public JSONObject putProblem() {
//
//    }

    public JSONObject getProblem(Integer id) {
        String url = "http://81.70.241.166:1233/api/problem?problem_id=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", X_Csrftoken);
        headers.add("Cookie", cookie);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        return getString(url, requestEntity);
    }

    public JSONObject submit(String input)  {
        String url = "http://81.70.241.166:1233/api/submission";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", X_Csrftoken);
        headers.add("Cookie", cookie);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(input.replace(' ', ' '), headers);
        JSONObject result =  postString(url, requestEntity);
        JSONObject data = result.getJSONObject("data");
        String submission_id = data.getString("submission_id");
        return getResult(submission_id);
    }

    public JSONObject getResult(String input) {
        String url = "http://81.70.241.166:1233/api/submission?id=" + input;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", X_Csrftoken);
        headers.add("Cookie", cookie);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        return getString(url, requestEntity);
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
    private static JSONObject postString(String url, HttpEntity<String> requestEntity) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JSONObject.class);

        HttpStatusCode statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new ServiceException("访问失败");
        }
    }
}