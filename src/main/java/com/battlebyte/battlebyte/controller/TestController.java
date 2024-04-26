package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/hello")
    public boolean hello() {

        return SecurityUtils.getSubject().isAuthenticated();
    }
    @GetMapping
    public int token(@RequestParam String token) {
        return JwtUtil.getUserId(token);
    }
}
