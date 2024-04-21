package com.battlebyte.battlebyte.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
    //注解将某个值注给下面的变量
    @Value("${emos.jwt.secret}")
    private String secret;
    @Value("${emos.jwt.expire}")
    private int expire;//保存的时间

    //1、通过唯一的openID生成令牌
    public String createToken(int userId){
        //1、确定保存时间
        /**第一个参数是当前时间，第二个参数是单位，第三个参数是保存时间*/
        Date date = DateUtil.offset(new Date(),
                DateField.DAY_OF_YEAR, 5);
        //2、根据密钥创建加密算法对象,使用JWT包下的
        Algorithm algorithm=Algorithm.HMAC256(secret);
        //3、创建加密内部类的对象
        JWTCreator.Builder builder= JWT.create();
        //4、内部类对象中的链式调用创建令牌
        String token= builder.withClaim("userId",userId) //用户信息
                .withExpiresAt(date)    //确定保存时间
                .sign(algorithm);       //使用的算法
        //5、返回令牌数据
        return token;
    }

    //2、通过令牌来解密出用户的openId
    public int getUserId(String token){
        //1、通过令牌创建解码对象
        DecodedJWT jwt = JWT.decode(token);
        //2、对象通过设置令牌withClaim的name属性获得Id，并且强转为int类型
        Integer userId = jwt.getClaim("userId").asInt();
        //3、返回唯一标识id
        return userId;
    }

    //3、验证令牌的有效性，如果出现问题就会自动抛出异常，RunTime异常
    public void verifierToken(String token){
        //1、传入密钥确定使用的算法
        Algorithm algorithm = Algorithm.HMAC256(secret);
        //2、用JWT的方法根据算法生成验证对象
        /**
         * 1、JWT.require(algorithm)进行解密
         * 2、.build来创建验证对象
         * */
        JWTVerifier verifier = JWT.require(algorithm).build();
        //3、验证对象调用verify方法进行验证
        verifier.verify(token);
    }
}