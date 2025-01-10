package com.teamb.authentication.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    private final String secretKey;

    public JWTService() {
        this.secretKey = "mHbLsmh+uFlpYOlg7doIys4aPSzj6CpJG0kNHtW/EXA="; // Replace with your actual secret key
    }

    public String generateToken(String email, String accountId) {
        Map<String, Object> claims = new HashMap<>();
        

        return JWT.create()
                .withClaim("email", email)
                .withClaim("accountId", accountId)
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 180 * 60 * 1000)) // Token valid for 3 hours
                .withPayload(claims)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String extractEmail(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean validateToken(String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            return tokenEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token)
                .getExpiresAt();
        return expiration.before(new Date());
    }
}
