package com.teamb.account.services;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.teamb.account.models.Account;
import com.teamb.account.repositories.AccountRepository;
import com.teamb.common.configurations.PasswordEncoding;
import com.teamb.common.exception.EntityNotFound;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoding passwordEncoding;

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    public ResponseEntity<Account> getAccountByAccountId(String accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        
        if (accountOptional.isPresent()) {
            return ResponseEntity.ok(accountOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // fetch account by email
    public Account getAccountByEmail(String email){
        return accountRepository.findByEmail(email);
    }

    // get account
    public Account updateAccount(Account account, String id){
        Account existingAccount = accountRepository.findById(id).orElseThrow(() -> 
            new RuntimeException("Account not found with id: " + account.getId())
        );

        // Update existing fields
        // existingAccount.setRole(account.getRole());
        existingAccount.setEmail(account.getEmail());
        existingAccount.setPassword(passwordEncoding.passwordEncoder().encode(account.getPassword()));
        existingAccount.setEmailVerified(account.getEmailVerified());
        existingAccount.setAdminCreated(account.getAdminCreated());

        // Update the updatedAt field
        existingAccount.setUpdatedAt(Instant.now());

        // Save the updated account
        return accountRepository.save(existingAccount);
    }

  


}
