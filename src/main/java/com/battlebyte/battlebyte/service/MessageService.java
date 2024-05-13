package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.FriendDao;
import com.battlebyte.battlebyte.dao.MessageDao;
import com.battlebyte.battlebyte.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private FriendService friendService;

    public void send(@RequestBody Integer receiver) {

    }

    public List<Message> receive(@RequestParam Integer receiver) {
        return null;
    }
}
