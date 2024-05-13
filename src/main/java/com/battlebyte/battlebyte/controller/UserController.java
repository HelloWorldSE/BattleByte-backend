package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.*;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // 注册用户
    @PostMapping("/auth/register")
    public void registerUser(@RequestBody User user) {
        user.setRating(0);
        user.setAvatar("default1.jpg");
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

    @GetMapping("/api/user")
    public Page<UserInfoDTO> getUser(@RequestParam(defaultValue = "0") Integer id, @RequestParam(defaultValue = "") String name,
                                     @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return userService.getUser(id, name, pageable);
    }
}