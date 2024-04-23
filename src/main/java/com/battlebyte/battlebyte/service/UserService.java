package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.config.UserToken;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.Role;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.LoginDTO;
import com.battlebyte.battlebyte.entity.dto.UserInfoDTO;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
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

    public void register(User user) {
        if (userDao.findByUserName(user.getUserName()) != null) {
            throw new ServiceException("用户名已存在");
        } else {
            User user1 = userDao.save(user);
            userDao.setRole(user1.getId(), 1); // default set role = 0
        }
    }

    public LoginDTO login(String username, String password) {
        User user = userDao.findByUserName(username);
        if (user == null) {
            throw new ServiceException("用户名不存在");
        }
        String token = JwtUtil.createToken(user.getId(), password);
        UserToken userToken = new UserToken(token);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(userToken);
        } catch (UnknownAccountException e) {
            throw new ServiceException("用户不存在");
        } catch (IncorrectCredentialsException e) {
            throw new ServiceException("密码错误");
        } catch (ExpiredCredentialsException e) {
            throw new ServiceException("token过期");
        }
        List<String> roles = userDao.getRole(user.getId());
        return new LoginDTO(token, roles.get(0));
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
