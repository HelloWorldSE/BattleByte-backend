package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    public User login(String username, String password) {
        User user = userDao.findUser(username, password);
        return user;
    }

    public void add(User user) {
        userDao.save(user);
    }

    public void register(User user) {
        if (userDao.findUserByName(user.getUserName()) != null) {

        } else {
            userDao.save(user);
        }
    }
}
