package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.common.Result;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.LoginDTO;
import com.battlebyte.battlebyte.entity.dto.UserDTO;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // 注册用户
    @PostMapping("/auth/register")
    public void registerUser(@RequestBody User user) {
        userService.register(user);
    }

    // 用户登录
    @PostMapping("/auth/login")
    public LoginDTO loginUser(@RequestBody UserDTO loginRequest) {
        return userService.login(loginRequest.getUserName(), loginRequest.getPassword());
    }

    @GetMapping("/api/user/profile")
    public UserProfileDTO getById(@RequestParam(defaultValue = "0") Integer id) {
        return userService.findByUserId(id);
    }

    @PostMapping("/api/user/update")
    public void update(@RequestBody User user) {
        userService.update(user);
    }

    @GetMapping("/nopermission")
    public void nopermission() {
        throw new ServiceException(2, "无权限");
    }
}