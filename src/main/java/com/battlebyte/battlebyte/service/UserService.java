package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    public void add(User user) {
        userDao.save(user);
    }

    public void register(User user) {
        if (userDao.findUserByName(user.getUserName()) != null) {

        } else {
            userDao.save(user);
        }
    }

    public User login(String username, String password) {
        User user = userDao.findUser(username, password);
        return user;
    }

    public User findById(Integer uid) {
        Optional<User> optionalUser = userDao.findById(uid);
        if (optionalUser.isEmpty()) {
            // wrong
            throw new ServiceException("用户未找到！");
        } else {
            return optionalUser.get();
        }
    }
}
