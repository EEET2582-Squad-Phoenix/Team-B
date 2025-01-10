package com.teamb.authentication.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.teamb.account.models.Account;
import com.teamb.account.repositories.AccountRepository;

@Service
public class AuthenticateUserService {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AccountRepository accountRepository;
    
    public String authenticateUser(String email, String password) {
        try {
            // Authenticate the user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // Fetch user account from the database
            Account account = accountRepository.findByEmail(email);
            if (account == null) {
                throw new RuntimeException("Account not found");
            }

            // Generate the JWT token
            return jwtService.generateToken(email, account.getId());
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
    
}
