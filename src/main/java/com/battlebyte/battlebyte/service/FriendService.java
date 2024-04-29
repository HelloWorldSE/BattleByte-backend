package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.FriendApplicationDao;
import com.battlebyte.battlebyte.dao.FriendDao;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.Friend;
import com.battlebyte.battlebyte.entity.FriendApplication;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
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

    @Transactional
    public Page<UserInfoDTO> getFriendApplications(Integer id, String name, Integer uid, Pageable pageable) {
        return friendApplicationDao.getFriendApplication(id, name, uid, pageable);
    }

    public void addFriend(Integer dest) {
        FriendApplication application = new FriendApplication();
        application.setSenderId(JwtUtil.getUserId());
        application.setReceiverId(dest);
        friendApplicationDao.save(application);
    }

    public void processApply(FriendApplication friendApplication, boolean accept) {
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
            if (friendDao.findFriend(smallId, largeId) != null) {
                throw new ServiceException("已是好友");
            }
            friendDao.save(friend);
        }
        friendApplicationDao.delete(friendApplication);
    }

    public void delFriend(Friend friend) {
        friendDao.delete(friend);
    }
}
