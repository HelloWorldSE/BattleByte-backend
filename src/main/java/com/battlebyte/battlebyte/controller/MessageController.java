package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Message;
import com.battlebyte.battlebyte.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/message")
@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;
    @PostMapping("/send")
    public void send(@RequestBody Integer receiver) {

    }

    @GetMapping()
    public List<Message> receive(@RequestParam Integer receiver) {
        return null;
    }

}
