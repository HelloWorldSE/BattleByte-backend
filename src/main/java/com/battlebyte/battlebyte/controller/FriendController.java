package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/user/friend")
public class FriendController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public Page<UserInfoDTO> getFriend(@RequestParam Integer uid,
                                       @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return userService.getFriend(uid, pageable);
    }

    @PostMapping("/add")
    public void addFriend() {

    }

    @PostMapping("/handle")
    public void handleFriend() {

    }
}
