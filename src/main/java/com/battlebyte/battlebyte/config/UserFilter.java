package com.battlebyte.battlebyte.config;

import cn.hutool.json.JSONObject;
import com.battlebyte.battlebyte.common.Result;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


@Slf4j
public class UserFilter extends BasicHttpAuthenticationFilter {

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws ServiceException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("token");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        if (token == null || "".equals(token)){
            Object result = Result.error(2, "识别用户信息失败");
            OutputStream os = response.getOutputStream();
            os.write(new ObjectMapper().writeValueAsString(result).getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            return false;
        }
        UserToken jwtToken = new UserToken(token);
        try {
            SecurityUtils.getSubject().login(jwtToken);
            return true;
        } catch (UnknownAccountException e) {
            Object result = Result.error(2, "用户名不存在");
            OutputStream os = response.getOutputStream();
            os.write(new ObjectMapper().writeValueAsString(result).getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            return false;
        } catch (IncorrectCredentialsException e) {
            Object result = Result.error(2, "无效的token");
            OutputStream os = response.getOutputStream();
            os.write(new ObjectMapper().writeValueAsString(result).getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            return false;
        } catch (ExpiredCredentialsException e){
            Object result = Result.error(2, "token已过期");
            OutputStream os = response.getOutputStream();
            os.write(new ObjectMapper().writeValueAsString(result).getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            return false;
        } catch (Exception e){
            Object result = Result.error(2, "未知错误");
            OutputStream os = response.getOutputStream();
            os.write(new ObjectMapper().writeValueAsString(result).getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            return false;
        }
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            return executeLogin(request, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


//    @Override
//    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        throw new ServiceException(2, "验证用户信息失败");
//    }

    /**
     * token认证executeLogin成功后，进入此方法，可以进行token更新过期时间
     */
//    @Override
//    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {

//    }
//    @Override
//    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
//        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
//        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
//        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
//        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
//            httpServletResponse.setStatus(HttpStatus.OK.value());
//            return false;
//        }
//        return super.preHandle(request, response);
//    }
}