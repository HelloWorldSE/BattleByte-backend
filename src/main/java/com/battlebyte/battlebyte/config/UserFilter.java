package com.battlebyte.battlebyte.config;

import com.battlebyte.battlebyte.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class UserFilter extends BasicHttpAuthenticationFilter {

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws ServiceException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("token");
        if (token == null || "".equals(token)){
            throw new ServiceException("无token，无权访问");
        }
        UserToken jwtToken = new UserToken(token);
        try {
            SecurityUtils.getSubject().login(jwtToken);
            return true;
        } catch (ExpiredCredentialsException e){
            throw new ServiceException("token过期");
        } catch (Exception e){
            throw new ServiceException("无效的token");
        }
    }

    /**
     * 过滤器拦截请求的入口方法
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        executeLogin(request, response);  //token验证
        return true;
    }

    /**
     * isAccessAllowed()方法返回false，即认证不通过时进入onAccessDenied方法
     */
//    @Override
//    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        return super.onAccessDenied(request, response);
//    }

    /**
     * token认证executeLogin成功后，进入此方法，可以进行token更新过期时间
     */
//    @Override
//    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {

//    }
}