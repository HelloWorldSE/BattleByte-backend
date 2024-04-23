package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.FriendApplicationDao;
import com.battlebyte.battlebyte.dao.FriendDao;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.FriendApplication;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendService {
    @Autowired
    private FriendDao friendDao;
    @Autowired
    private FriendApplicationDao friendApplicationDao;

    public void addFriend(Integer dest) {
        FriendApplication application = new FriendApplication();
        application.setSenderId(JwtUtil.getUserId());
        application.setReceiverId(dest);
        friendApplicationDao.save(application);
    }
}
