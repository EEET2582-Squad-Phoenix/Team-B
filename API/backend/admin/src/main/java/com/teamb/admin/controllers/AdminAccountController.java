package com.teamb.admin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.authentication.services.AuthenticateService;


@RestController
@RequestMapping("/admin/account")
public class AdminAccountController {
    
    @Autowired
    private AuthenticateService service;

    @DeleteMapping("/delete/{id}")
    public String deleteAccount(@PathVariable String id){
        return service.deleteAccount(id);
    }
}
