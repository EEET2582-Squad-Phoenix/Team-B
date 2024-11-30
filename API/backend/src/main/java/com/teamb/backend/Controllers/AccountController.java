package com.teamb.backend.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.backend.Models.Account;
import com.teamb.backend.Models.Registration;
import com.teamb.backend.Services.AccountService;


@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService service;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            // Authenticate the user (check if the credentials are valid)
            String result = service.authenticateUser(email, password);

            if (result != null) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    // @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createAccount(@RequestBody Registration registration) {
        try {
            Account saved = service.registerUser(registration);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved + "/nRegistration successful. Please verify your email.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Account> getAllAccounts(){
        return service.getAllAccounts();
    }

    @GetMapping("/getById/{id}")
    public Account getAccountById(@PathVariable String id){
        return service.getAccountByAccountId(id);
    }


    @PutMapping("/edit/{id}")
    public Account modifyAccount(@RequestBody Account account, @PathVariable String id){
        return service.updateAccount(account, id);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            String result = service.verifyEmail(token);
            if (result.equals("Verified successfully")) {
                return ResponseEntity.status(HttpStatus.OK).body("Email verified successfully. Please login.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during verification: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAccount(@PathVariable String id){
        return service.deleteAccount(id);
    }
}
