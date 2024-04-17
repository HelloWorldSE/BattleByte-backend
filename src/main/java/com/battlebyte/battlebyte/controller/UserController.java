package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.common.Result;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.LoginDTO;
import com.battlebyte.battlebyte.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 注册用户
    @PostMapping("/register")
    public Result registerUser(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    // 用户登录
    @PostMapping("/login")
    public Result loginUser(@RequestBody LoginDTO loginRequest) {
        userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return Result.success();
    }
}