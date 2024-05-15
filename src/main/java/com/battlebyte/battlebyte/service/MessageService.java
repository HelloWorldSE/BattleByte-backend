package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.FriendDao;
import com.battlebyte.battlebyte.dao.MessageDao;
import com.battlebyte.battlebyte.entity.Message;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private FriendService friendService;

    public void send(Integer sender, Integer receiver, String content) {
        Message message = new Message();
        message.setDate(new Date());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessage(content);
        message.setTag(0); // tag0: 普通消息
        message.setRead(false); // 未读
        messageDao.save(message);
    }

    public Page<Message> findMessagesBySender(Integer sender, Pageable pageable) {
        return messageDao.findMessagesBySender(sender, pageable);
    }
    public Page<Message> receive(Integer receiver, Pageable pageable) {
        return messageDao.findMessagesByReceiver(receiver, pageable);
    }

    public void read(Integer id) {
        Message message = messageDao.findById(id).orElse(null);
        if (message == null || message.getReceiver() != JwtUtil.getUserId()) {
            throw new ServiceException("信息不存在！");
        }
        message.setRead(true);
        messageDao.save(message);
    }
}
