package com.teamb.authentication.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserService {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;
    
    public String authenticateUser(String email, String password) {
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            return jwtService.generateToken(email);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        } catch (Exception e) {
            throw new RuntimeException("Email is not verifed");
        }
    }
    
}
