package com.teamb.account.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.account.models.Account;
import com.teamb.account.services.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService service;

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

    @DeleteMapping("/delete/{id}")
    public String deleteAccount(@PathVariable String id){
        return service.deleteAccount(id);
    }
}
