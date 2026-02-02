package com.saksham_kumar;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private static final String SECRET_KEY = "RoadmapToBackendEngineeringIsSuperCoolAndLongEnough";

    public String getJWT(String userName) {
        String token = Jwts.builder().setSubject(userName).setIssuedAt(new Date()).setExpiration(new Date(1000 * 60 * 60)).signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).compact();

        return token;
    }
}
