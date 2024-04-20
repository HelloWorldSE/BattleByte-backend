package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.common.Result;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.LoginDTO;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // 注册用户
    @PostMapping("/auth/register")
    public Result registerUser(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    // 用户登录
    @PostMapping("/auth/login")
    public Result loginUser(@RequestBody LoginDTO loginRequest) {
//        User user = userService.login(loginRequest.getUserName(), loginRequest.getPassword());
//        if (user != null) {
//            Subject subject = SecurityUtils.getSubject();
//            //封装用户的登录数据
//            UsernamePasswordToken token = new UsernamePasswordToken(loginRequest.getUserName(), loginRequest.getPassword());
//            return Result.success();
//        } else {
//            throw new ServiceException("用户名或密码错误");
//        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginRequest.getUserName(), loginRequest.getPassword());
        try {
            subject.login(token);
        } catch (UnknownAccountException e) {
            throw new ServiceException("账号不存在");
        } catch (IncorrectCredentialsException e) {
            throw new ServiceException("密码错误");
        }
        return Result.success();
    }

    @PostMapping("/api/profile")
    public Result getById(Integer id) {
        return Result.success(userService.findById(id));
    }

    @GetMapping("/test")
    public Result test() {
        return Result.success(userService.getRole(1));
    }

    @GetMapping("/test2")
    public Result test2() {
        return Result.success(userService.getPermission(1));
    }
}