package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.common.Result;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.UserDTO;
import com.battlebyte.battlebyte.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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

    public User findByUserId(Integer uid) {
        User user = userDao.findByUserId(uid);
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

    public Page<UserDTO> getFriend(Integer uid, Pageable pageable) {
        return userDao.findFriend(uid, pageable);
    }
}
