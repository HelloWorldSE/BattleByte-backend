package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Friend;
import com.battlebyte.battlebyte.entity.FriendApplication;
import com.battlebyte.battlebyte.entity.dto.FriendDTO;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.service.FriendService;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.JwtUtil;
import com.battlebyte.battlebyte.websocket.WebSocketServer;
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
    public Page<FriendDTO> getFriend(@RequestParam(defaultValue = "0") Integer id, @RequestParam(defaultValue = "") String name,
                                     @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return friendService.getFriend(id, name, JwtUtil.getUserId(), pageable);
    }

    // 发送好友申请
    @PostMapping("/add-apply")
    public void addFriend(@RequestBody Integer dest) {
        friendService.addFriend(dest);
        new WebSocketServer().sendFriendInvitation(JwtUtil.getUserId(), dest);
    }

    // 获取所有好友申请
    @GetMapping("/apply")
    public Page<FriendDTO> getFriendApplications(@RequestParam(defaultValue = "0") Integer id, @RequestParam(defaultValue = "") String name,
                                                   @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return friendService.getFriendApplications(id, name, JwtUtil.getUserId(), pageable);
    }


    @PostMapping("/process")
    public void process(@RequestBody Integer id, @RequestParam boolean accept) {
        friendService.processApply(id, accept);
    }

    @DeleteMapping("/{id}")
    public void delFriend(@PathVariable("id") Integer id) {
        friendService.delFriend(id);
    }
}
