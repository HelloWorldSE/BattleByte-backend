package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.dao.FriendApplicationDao;
import com.battlebyte.battlebyte.dao.FriendDao;
import com.battlebyte.battlebyte.entity.FriendApplication;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.service.FriendService;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/friend")
public class FriendController {
    @Autowired
    private UserService userService;
    @Autowired
    private FriendService friendService;

    @GetMapping("")
    public Page<UserInfoDTO> getFriend(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return userService.getFriend(JwtUtil.getUserId(), pageable);
    }

    @PostMapping("/add-apply")
    public void addFriend(@RequestBody Integer dest) {
        friendService.addFriend(dest);
    }

    @PostMapping("/")
    public void handleFriend() {

    }
}
