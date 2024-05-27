package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.FriendApplicationDao;
import com.battlebyte.battlebyte.dao.FriendDao;
import com.battlebyte.battlebyte.entity.Friend;
import com.battlebyte.battlebyte.entity.FriendApplication;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.FriendDTO;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendService {
    @Autowired
    private FriendDao friendDao;
    @Autowired
    private FriendApplicationDao friendApplicationDao;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @Transactional
    public Page<FriendDTO> getFriendApplications(Integer id, String name, Integer uid, Pageable pageable) {
        return friendApplicationDao.getFriendApplication(id, name, uid, pageable);
    }

    public Page<FriendDTO> getFriend(Integer id, String name, Integer uid, Pageable pageable) {
        return friendDao.findFriend(id, name, uid, pageable);
    }

    public void addFriend(Integer dest) {
        int sender = JwtUtil.getUserId();
        User user = userService.findById(sender);
        FriendApplication application = new FriendApplication();

        int smallId = sender;
        int largeId = dest;
        if (smallId > largeId) {
            int temp = smallId;
            smallId = largeId;
            largeId = temp;
        }

        if (friendDao.findFriendById(smallId, largeId) != null) {
            throw new ServiceException("已是好友");
        }

        if (friendApplicationDao.getOne(sender, dest) != null) {
            throw new ServiceException("好友申请已存在");
        }

        FriendApplication friendApplication = friendApplicationDao.getOne(dest, sender);
        if (friendApplication != null) {
            processApply(friendApplication.getId(), true);
            return;
        }
        application.setSenderId(sender);
        application.setReceiverId(dest);
        String text = user.getUserName() + "向你发送了好友申请！";
        messageService.send(JwtUtil.getUserId(), dest, text, 1);
        friendApplicationDao.save(application);
    }

    public void processApply(Integer id, boolean accept) {
        FriendApplication friendApplication = friendApplicationDao.findById(id).orElse(null);
        if (friendApplication == null) {
            throw new ServiceException("未找到此请求");
        }
        int smallId = friendApplication.getSenderId();
        int largeId = friendApplication.getReceiverId();

        if (smallId > largeId) {
            int temp = smallId;
            smallId = largeId;
            largeId = temp;
        }

        if (accept) {
            Friend friend = new Friend();
            friend.setSmallId(smallId);
            friend.setLargeId(largeId);
            if (friendDao.findFriendById(smallId, largeId) != null) {
                throw new ServiceException("已是好友");
            }
            friendDao.save(friend);
        }
        friendApplicationDao.deleteById(id);
    }

    public void delFriend(Integer id) {
        friendDao.deleteById(id);
    }
}
