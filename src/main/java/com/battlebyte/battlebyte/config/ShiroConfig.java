//package com.battlebyte.battlebyte.config;
//
//import com.battlebyte.battlebyte.realm.UserRealm;
//import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
//import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class ShiroConfig {
//
//    @Bean
//    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
//        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
//        shiroFilter.setSecurityManager(defaultWebSecurityManager);
//        shiroFilter.setUnauthorizedUrl("/nopermission"); // 设置无权限页面
//        Map<String, String> permission = new HashMap<>();
//        permission.put("/api/user/login", "anon");
//        permission.put("/**", "authc");
//        // permission.put("/**", "anon");
//        shiroFilter.setFilterChainDefinitionMap(permission);
//        return shiroFilter;
//    }
//
//    @Bean
//    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
//        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
//        manager.setRealm(userRealm);
//        return manager;
//    }
//
//    @Bean
//    public UserRealm userRealm() {
//        return new UserRealm();
//    }
//}
