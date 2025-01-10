package com.teamb.authentication.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    private String secretKey = "mHbLsmh+uFlpYOlg7doIys4aPSzj6CpJG0kNHtW/EXA=";
    private final Algorithm algorithm = Algorithm.HMAC256(secretKey);

    public JWTService() {
        
    }

    public String generateToken(String email, String accountId) {
        Map<String, Object> claims = new HashMap<>();
        

        return JWT.create()
                .withClaim("email", email)
                .withClaim("accountId", accountId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + (10 * 24 * 60 * 60 * 1000)))
                .sign(algorithm);
    }

    public String extractEmail(String token) {
        return JWT.require(algorithm)
            .build()
            .verify(token)
            .getClaim("email")  // Extract the email from the claims
            .asString();
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