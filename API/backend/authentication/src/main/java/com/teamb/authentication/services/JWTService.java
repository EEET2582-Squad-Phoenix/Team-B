package com.teamb.authentication.services;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    private String secretKey = "T9s4/KBX8vjsXPyUou2IWiJtvnpn7W5UK983YO6avSs=";

    public JWTService() {
        // Initialize if needed (removed KeyGenerator code for simplicity)
    }

    public String generateToken(String email, String accountId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
     
        // Set claims (payload)
        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", accountId);
        claims.put("email", email);
    
        // Set expiration time (10 days in milliseconds)
        long expirationTime = 10 * 24 * 60 * 60 * 1000;
    
        // Generate the token using the HS256 algorithm
        return Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)  // Set claims without the subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getKey(), SignatureAlgorithm.HS256)  // Use HS256 as the algorithm
                .compact();
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
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
