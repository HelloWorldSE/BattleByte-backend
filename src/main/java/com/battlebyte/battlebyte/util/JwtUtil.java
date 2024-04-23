package com.battlebyte.battlebyte.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    public static String createToken(int userId, String secret){
        Date date = new Date(System.currentTimeMillis() + 72 * 3600 * 1000);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create().withClaim("userId", userId) //用户信息
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