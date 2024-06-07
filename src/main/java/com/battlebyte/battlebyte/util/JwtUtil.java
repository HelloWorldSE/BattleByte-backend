package com.battlebyte.battlebyte.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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

    /** 无参函数：根据请求查询id
     *  有参函数：根据传入的token查询id
     */
    public static int getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        return getUserId(token);
    }

    public static int getUserId(String token){
        DecodedJWT jwt = JWT.decode(token);
        Integer userId = jwt.getClaim("userId").asInt();
        return userId;
    }

    public static boolean verifierToken(String token, int id, String secret) {
        try {
            System.out.printf("%d %s\n", id, secret);

            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("userId", id)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    public static boolean isExpire(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis();
    }
}