package com.battlebyte.battlebyte.realm;

import com.battlebyte.battlebyte.config.UserToken;
import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UserToken;
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) principalCollection.getPrimaryPrincipal();
        int userId = user.getId();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        HashSet<String> roles = new HashSet<>(userService.getRole(userId));
        HashSet<String> permissions = new HashSet<>(userService.getPermission(userId));
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException { // 认证：用户的权限信息
        String accessToken = (String) token.getPrincipal();
        int userId = JwtUtil.getUserId(accessToken);
        User user = userService.findById(userId);

        if (user == null) {
            throw new UnknownAccountException();
        }

        if (!JwtUtil.verifierToken(accessToken, userId, user.getPassword())) {
            throw new IncorrectCredentialsException();
        }

        if (JwtUtil.isExpire(accessToken)) {
            throw new ExpiredCredentialsException();
        }

        return new SimpleAuthenticationInfo(user, accessToken, getName());
    }
}
