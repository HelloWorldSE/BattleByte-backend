package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.MessageDao;
import com.battlebyte.battlebyte.entity.Message;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 规定消息的类型如下：
 * 0: 普通消息
 * 1: 好友请求
 */

@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;
    
    private final FriendService friendService;
    @Autowired
    public MessageService(@Lazy FriendService friendService) {
        this.friendService = friendService;
    }

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

    public void send(Integer sender, Integer receiver, String content, Integer tag) {
        Message message = new Message();
        message.setDate(new Date());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessage(content);
        message.setTag(tag);
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
        if (message == null || !access(message)) {
            throw new ServiceException("信息不存在！");
        }
        message.setRead(true);
        messageDao.save(message);
    }

    private boolean access(Message message) {
        if (message.getReceiver() != JwtUtil.getUserId() && message.getReceiver() != -1) {
            return false;
        } else {
            return true;
        }
    }
}
