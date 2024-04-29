package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Friend;
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
    public Page<UserInfoDTO> getFriend(@RequestParam(defaultValue = "0") Integer id, @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return userService.getFriend(id, name, JwtUtil.getUserId(), pageable);
    }

    @PostMapping("/add-apply")
    public void addFriend(@RequestBody Integer dest) {
        friendService.addFriend(dest);
    }

    @GetMapping("/apply")
    public Page<UserInfoDTO> getFriendApplications(@RequestParam(defaultValue = "0") Integer id, @RequestParam(defaultValue = "") String name,
                                                   @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return friendService.getFriendApplications(id, name, JwtUtil.getUserId(), pageable);
    }


    @PostMapping("/process")
    public void process(@RequestBody FriendApplication friendApplication, @RequestParam boolean accept) {
        friendService.processApply(friendApplication, accept);
    }

    @PostMapping("/delete")
    public void delFriend(@RequestBody Friend friend) {
        friendService.delFriend(friend);
    }
}
