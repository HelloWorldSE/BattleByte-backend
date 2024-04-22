package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.common.Result;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.UserDTO;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
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
        userService.register(user);
    }

    // 用户登录
    @PostMapping("/auth/login")
    public void loginUser(@RequestBody UserDTO loginRequest) {
        User user = userService.login(loginRequest.getUserName(), loginRequest.getPassword());
        if (user != null) {
            return;
        } else {
            throw new ServiceException("用户名或密码错误");
        }
    }

    @GetMapping("/api/user/profile")
    public UserProfileDTO getById(@RequestParam Integer id) {
        return userService.findByUserId(id);
    }



    @PostMapping("/api/user/update")
    public void update(@RequestBody User user) {
        userService.update(user);
    }
}