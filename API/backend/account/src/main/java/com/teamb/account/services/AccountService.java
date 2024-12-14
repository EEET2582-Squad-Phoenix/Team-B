package com.teamb.account.services;

import java.util.List;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamb.account.models.Account;
import com.teamb.account.repositories.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;



    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    public Account getAccountByAccountId(String accountId){
        return accountRepository.findById(accountId).get();
    }

   
    public Account updateAccount(Account account, String id){
        Account existingAccount = accountRepository.findById(id).orElseThrow(() -> 
            new RuntimeException("Account not found with id: " + account.getId())
        );

        // Update existing fields
        existingAccount.setRole(account.getRole());
        existingAccount.setEmailVerified(account.getEmailVerified());
        existingAccount.setAdminCreated(account.getAdminCreated());

        // Update the updatedAt field
        existingAccount.setUpdatedAt(Instant.now());

        // Save the updated account
        return accountRepository.save(existingAccount);
    }

    public String deleteAccount(String accountId){
        accountRepository.deleteById(accountId);
        return "Account with id " + accountId + " deleted";
    }
}
