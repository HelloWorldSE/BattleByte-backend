//package com.battlebyte.battlebyte.config;
//
//import com.battlebyte.battlebyte.exception.ServiceException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.ExpiredCredentialsException;
//import org.apache.shiro.lang.ShiroException;
//import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//
//@Slf4j
//@Component
//public class UserFilter extends BasicHttpAuthenticationFilter {
//
//    /**
//     * 进行token的验证
//     */
//    @Override
//    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
//        //在请求头中获取token
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        String token = httpServletRequest.getHeader("token"); //前端命名Authorization
//        //token不存在
//        if (token == null || "".equals(token)){
//            throw new ServiceException(2, "无token，无权访问");
//        }
//
//        //token存在，进行验证
//        UserToken jwtToken = new UserToken(token);
//        try {
//            SecurityUtils.getSubject().login(jwtToken);  //通过subject，提交给myRealm进行登录验证
//            return true;
//        } catch (ExpiredCredentialsException e){
//            throw new ServiceException(2, "token过期");
//        } catch (ShiroException e){
//            throw new ServiceException(2, "无效的token");
//        }
//    }
//
//    /**
//     * 过滤器拦截请求的入口方法
//     */
//    @Override
//    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//        try {
//            return executeLogin(request, response);  //token验证
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * isAccessAllowed()方法返回false，即认证不通过时进入onAccessDenied方法
//     */
////    @Override
////    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
////        return super.onAccessDenied(request, response);
////    }
//
//    /**
//     * token认证executeLogin成功后，进入此方法，可以进行token更新过期时间
//     */
////    @Override
////    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
//
////    }
//}