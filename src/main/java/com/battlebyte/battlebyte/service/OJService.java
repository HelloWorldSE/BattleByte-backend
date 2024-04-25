package com.battlebyte.battlebyte.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.exception.ServiceException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OJService {
    public String getProblem(Integer id) {
        String url = "http://81.70.241.166:1233/api/admin/problem?id=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", "vLH0iNGPu4ufpc9Lk1ekq5VmshkEM6a2aw6FGvxFZxz29Lqat88S4vfiHjQnNoV2");
        headers.add("Cookie", "_pk_id.1.7ebb=d3c89c8c7f0158ca.1713614493.; " +
                "csrftoken=vLH0iNGPu4ufpc9Lk1ekq5VmshkEM6a2aw6FGvxFZxz29Lqat88S4vfiHjQnNoV2; " +
                "sessionid=fowtr0r4jrylvsyybkd963kso2cu97am");

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        return getString(url, requestEntity);
    }

    public String submit(String input) {
        String url = "http://81.70.241.166:1233/api/submission";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", "vLH0iNGPu4ufpc9Lk1ekq5VmshkEM6a2aw6FGvxFZxz29Lqat88S4vfiHjQnNoV2");
        headers.add("Cookie", "_pk_id.1.7ebb=d3c89c8c7f0158ca.1713614493.; " +
                "csrftoken=vLH0iNGPu4ufpc9Lk1ekq5VmshkEM6a2aw6FGvxFZxz29Lqat88S4vfiHjQnNoV2; " +
                "sessionid=fowtr0r4jrylvsyybkd963kso2cu97am");

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(input.replace(' ', ' '), headers);
        String result =  getString(url, requestEntity);

        JSONObject resultObj = JSON.parseObject(result);
        JSONObject data = resultObj.getJSONObject("data");
        String submission_id = data.getString("submission_id");
        return getResult(submission_id);
    }

    public String getResult(String input) {
        String url = "http://81.70.241.166:1233/api/submission?submission_id=" + input;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Csrftoken", "vLH0iNGPu4ufpc9Lk1ekq5VmshkEM6a2aw6FGvxFZxz29Lqat88S4vfiHjQnNoV2");
        headers.add("Cookie", "_pk_id.1.7ebb=d3c89c8c7f0158ca.1713614493.; " +
                "csrftoken=vLH0iNGPu4ufpc9Lk1ekq5VmshkEM6a2aw6FGvxFZxz29Lqat88S4vfiHjQnNoV2; " +
                "sessionid=fowtr0r4jrylvsyybkd963kso2cu97am");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        return getString(url, requestEntity);
    }

    public static String getString(String url, HttpEntity<String> requestEntity) {
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
