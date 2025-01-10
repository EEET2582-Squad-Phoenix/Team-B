package com.teamb.authentication.services;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    private String secretKey = "mHbLsmh+uFlpYOlg7doIys4aPSzj6CpJG0kNHtW/EXA=";
    private final Algorithm algorithm = Algorithm.HMAC256(secretKey);

    public JWTService() {
        // Initialize if needed (removed KeyGenerator code for simplicity)
    }

   public String generateToken(String email, String accountId) {
        // Generate the token
        return JWT.create()
                .withClaim("email", email)
                .withClaim("accountId", accountId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + (10 * 24 * 60 * 60 * 1000))) // 10 days
                .sign(algorithm);
    }
    

    private SecretKey getKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);  // Using HMAC-SHA256 as in Node.js
    }

    public String extractEmail(String token) {
        // Extract the email from the JWT token
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractId(String token) {
        // Extract the accountId from the JWT token
        return extractClaim(token, claims -> claims.get("accountId", String.class));
    }

    private <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())  // Use the same key to verify the token
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        final String userName = extractEmail(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token)
                .getExpiresAt();
        return expiration.before(new Date());
    }
}
