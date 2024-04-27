package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.service.OJService;
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
    public String get(@RequestParam Integer id) throws InterruptedException {
        return ojService.getProblem(id);
    }

    @PostMapping("/submit")
    public String submit(@RequestBody String input) throws InterruptedException {
        return ojService.submit(input);
    }
}