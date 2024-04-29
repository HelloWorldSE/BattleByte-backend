package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    public UserService userService;

    @GetMapping("/id")
    public int token(@RequestParam String token) {
        return JwtUtil.getUserId(token);
    }
    @GetMapping("/role")
    public List<String> getRole() {
        return userService.getRole(JwtUtil.getUserId());
    }
}
