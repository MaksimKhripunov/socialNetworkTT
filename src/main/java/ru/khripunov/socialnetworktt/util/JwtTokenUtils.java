package ru.khripunov.socialnetworktt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;



import java.time.Duration;
import java.util.Date;



@Component
@RequiredArgsConstructor
public class JwtTokenUtils {



    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    public String generateToken(User person){

        Date issuedDate=new Date();
        Date expireDate=new Date(issuedDate.getTime() + jwtLifetime.toMillis());

        return Jwts.builder()
                .setSubject(person.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }


    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
