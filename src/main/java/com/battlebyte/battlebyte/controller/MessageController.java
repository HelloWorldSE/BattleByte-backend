package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Message;
import com.battlebyte.battlebyte.entity.dto.MessageDTO;
import com.battlebyte.battlebyte.service.MessageService;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/message")
@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;
    @PostMapping("/send")
    public void send(@RequestBody MessageDTO messageDTO) {
        messageService.send(JwtUtil.getUserId(), messageDTO.getId(), messageDTO.getContent());
    }

    // TODO: 目前该接口向所有人开放！
    @PostMapping("/broadcast")
    public void broadcast(@RequestBody String content) {
        messageService.send(JwtUtil.getUserId(), -1, content);
    }

    @GetMapping()
    public Page<Message> receive(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return messageService.receive(JwtUtil.getUserId(), pageable);
    }

    @GetMapping("/read")
    public void read(@RequestBody Integer id) {
        messageService.read(id);
    }

}
