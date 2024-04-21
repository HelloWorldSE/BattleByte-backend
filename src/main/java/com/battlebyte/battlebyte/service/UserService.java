package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.common.Result;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import com.battlebyte.battlebyte.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    public void add(User user) {
        userDao.save(user);
    }

    public Result register(User user) {
        if (userDao.findByUserName(user.getUserName()) != null) {
            throw new ServiceException("用户名已存在");
        } else {
            User user1 = userDao.save(user);
            userDao.setRole(user1.getId(), 1); // default set role = 0
        }
        return Result.success();
    }

    public User login(String username, String password) {
        User user = userDao.findUser(username, password);
        return user;
    }

    public User findById(Integer uid) {
        Optional<User> op = userDao.findById(uid);
        return op.orElse(null);
    }

    public UserProfileDTO findByUserId(Integer uid) {
        UserProfileDTO user = userDao.findByUserId(uid);
        if (user == null) {
            throw new ServiceException("用户未找到");
        } else {
            return user;
        }
    }

    public List<String> getRole(Integer uid) {
        return userDao.getRole(uid);
    }

    public List<String> getPermission(Integer uid) {
        return userDao.getPermission(uid);
    }

    public Page<UserInfoDTO> getFriend(Integer uid, Pageable pageable) {
        return userDao.findFriend(uid, pageable);
    }

    public void update(User user) {
        if (!userDao.existsById(user.getId())) {
            throw new ServiceException("用户不存在！");
        } else {
            userDao.save(user);
        }
    }

}
