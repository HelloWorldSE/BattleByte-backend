package com.battlebyte.battlebyte.controller;

import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.oj.ProblemContainer;
import com.battlebyte.battlebyte.service.OJService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oj")
public class OJController {

    @Autowired
    private OJService ojService;
    @GetMapping("/problem")
    public JSONObject get(@RequestParam Integer id){
        return ojService.getProblem(id);
    }
    @GetMapping("/problems")
    public JSONObject get(){
        return ojService.getAllProblems();
    }
    @PostMapping("/submit")
    public JSONObject submit(@RequestBody String input) {
        return ojService.submit(input);
    }

    @GetMapping("/search")
    public ProblemContainer search(@RequestParam String tag) throws JsonProcessingException {
        return ojService.search(tag);
    }
}