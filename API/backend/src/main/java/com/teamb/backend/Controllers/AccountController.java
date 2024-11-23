package com.teamb.backend.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.backend.Models.Account;
import com.teamb.backend.Repositories.AccountRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AccountController {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody Account account){
        try {
            if (accountRepository.findByEmail(account.getEmail()).isPresent())
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email has been registered already!");
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            Account save = accountRepository.save(account);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
