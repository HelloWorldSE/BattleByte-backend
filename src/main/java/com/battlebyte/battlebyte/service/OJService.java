package com.battlebyte.battlebyte.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Service
public class OJService {
    public String cookie;
    public String X_Csrftoken;
    public HashMap<Integer,Integer>problems=new HashMap<>();//<id:_id>
    public void login(){
        String filePath = "/home/ubuntu/BattleByte-backend/token.txt";
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            cookie = reader.readLine();
            X_Csrftoken = reader.readLine();

        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭 BufferedReader
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void updateProblems () {
        String url = "http://81.70.241.166:1233/api/admin/problem?limit=100&offset=0";
        login();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", X_Csrftoken);
        headers.add("Cookie", cookie);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        JSONArray results=getString(url, requestEntity).getJSONObject("data").getJSONArray("results");
        HashMap<Integer,Integer>newProblems=new HashMap<>();
        for (int i = 0; i < results.size(); i++) {
            LinkedHashMap<String, Object> t = (LinkedHashMap<String, Object>) results.get(i);
            newProblems.put((Integer) t.get("id"),Integer.parseInt((String) t.get("_id")));
        }
        problems=newProblems;
    }

    public JSONObject getProblem(Integer id) {
        String url = "http://81.70.241.166:1233/api/problem?problem_id=" + id;
        login();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", X_Csrftoken);
        headers.add("Cookie", cookie);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        return getString(url, requestEntity);
    }

    public JSONObject submit(String input)  {
        String url = "http://81.70.241.166:1233/api/submission";
        login();
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

    public static void main(String[] args) {
        OJService ojService=new OJService();
        ojService.updateProblems();
    }
}