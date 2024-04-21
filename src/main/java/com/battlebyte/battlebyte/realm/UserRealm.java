package com.battlebyte.battlebyte.realm;

import com.battlebyte.battlebyte.config.UserToken;
import com.battlebyte.battlebyte.entity.User;
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

@Component
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private JwtUtil jwtUtil;
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
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        HashSet<String> roles = new HashSet<>(userService.getRole(userId));
        HashSet<String> permissions = new HashSet<>(userService.getPermission(userId));
        simpleAuthorizationInfo.setRoles(roles);
        simpleAuthorizationInfo.setStringPermissions(permissions);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException { // 认证：用户的权限信息
        String accessToken = (String) authenticationToken.getPrincipal();
        int userId = jwtUtil.getUserId(accessToken);
        User user = userService.findById(userId);
        if (user != null) {
            return new SimpleAuthenticationInfo(user, accessToken, getName());
        } else {
            throw new LockedAccountException("用户不存在");
        }
    }
}
