package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
    @GetMapping
    public int token(@RequestParam String token) {
        return JwtUtil.getUserId(token);
    }
}
