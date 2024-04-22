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
    @Value("${emos.jwt.expire}")
    private static int expire;//保存的时间

    //1、通过唯一的openID生成令牌
    public static String createToken(int userId, String secret){
        Date date = DateUtil.offset(new Date(),
                DateField.SECOND, expire * 24 * 60 * 60);
        Algorithm algorithm=Algorithm.HMAC256(secret);
        JWTCreator.Builder builder= JWT.create();
        String token= builder.withClaim("userId", userId) //用户信息
                .withExpiresAt(date)
                .sign(algorithm);
        return token;
    }

    public static int getUserId(String token){
        DecodedJWT jwt = JWT.decode(token);
        Integer userId = jwt.getClaim("userId").asInt();
        return userId;
    }

    public static boolean verifierToken(String token, int id, String secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("userId", id)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean isExpire(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis();
    }
}