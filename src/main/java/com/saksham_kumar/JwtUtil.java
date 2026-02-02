package com.saksham_kumar;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private static final String SECRET_KEY = "RoadmapToBackendEngineeringIsSuperCoolAndLongEnough";

    public String getJWT(String userName) {
        String token = Jwts.builder().setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60))).signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).compact();

        return token;
    }

    public String userNameVerify(String token) {
        try {
            return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
