package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.config.UserToken;
import com.battlebyte.battlebyte.dao.FindPasswordDao;
import com.battlebyte.battlebyte.dao.UserDao;
import com.battlebyte.battlebyte.entity.FindPassword;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.entity.dto.*;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.util.JwtUtil;
import com.battlebyte.battlebyte.util.RsaUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    FindPasswordDao findPasswordDao;

    @Transactional
    public void register(User user) {
        if (userDao.findByUserName(user.getUserName()) != null) {
            throw new ServiceException("用户名已存在");
        } else {
            passwordCheck(user);
            User user1 = userDao.save(user);
            userDao.setRole(user1.getId(), 1); // default set role = user
        }
    }

    @Transactional
    public LoginDTO login(String username, String password) {
        User user = userDao.findByUserName(username);

        if (user == null) {
            throw new ServiceException("用户名不存在");
        }

        try {
            password = RsaUtils.decrypt(password);
        } catch (Exception e) {
            throw new ServiceException(2, "密码无法解密");
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

    @Transactional
    public void update(User user) {
        if (user.getId() != JwtUtil.getUserId()) {
            throw new ServiceException("");
        }
        if (!userDao.existsById(user.getId())) {
            throw new ServiceException("用户不存在！");
        }
        if (!(user.getPassword() == null || user.getPassword() == "")) {
            passwordCheck(user);
        } else {
            user.setPassword(null);
        }
        userDao.save(user);
    }

    private void passwordCheck(User user) {
        String password;
        try {
            password = RsaUtils.decrypt(user.getPassword());
        } catch (Exception e) {
            throw new ServiceException(2, "密码无法解密");
        }

        if (password == null || password.equals("")) {
            throw new ServiceException("请填写密码！");
        }

        if (password.length() <= 5) {
            throw new ServiceException("密码长度过短！");
        }

        if (password.length() >= 20) {
            throw new ServiceException("密码长度过长！");
        }

        if (password.length() < 2) {
            throw new ServiceException("用户名长度过短！");
        }

        if (password.length() >= 20) {
            throw new ServiceException("用户名长度过长！");
        }
        user.setPassword(password);
    }

    public User findById(Integer uid) {
        Optional<User> op = userDao.findById(uid);
        return op.orElse(null);
    }

    public UserProfileDTO findByUserId(Integer uid) {
        if (uid <= 0) {
            uid = JwtUtil.getUserId();
        }
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


    public Page<UserInfoDTO> getUser(Integer id, String name, Pageable pageable) {
        return userDao.findUser(id, name, pageable);
    }

    @Modifying
    @Transactional
    public void setRating(Integer uid, Integer rating) {
        User user = findById(uid);
        user.setRating(rating);
        userDao.save(user);
    }

    public void addRating(Integer uid, Integer offset) {
        User user = findById(uid);
        user.setRating(user.getRating() + offset);
        userDao.save(user);
    }

    @Transactional
    public void changePassword(PasswordDTO passwordDTO) {
        FindPassword findPassword = findPasswordDao.findById(passwordDTO.getId()).orElse(null);
        if (findPassword == null || !Objects.equals(findPassword.getVerify(), passwordDTO.getVerify()) ||
            new Date().getTime() - findPassword.getDate().getTime() > 5 * 60 * 1000) {
            throw new ServiceException("验证失败");
        } else {
            User user = new User();
            user.setId(findPassword.getId());
            user.setPassword(passwordDTO.getPassword());
            this.update(user);
        }
    }
}
