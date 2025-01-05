package com.teamb.charity.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.charity.models.Charity;
import com.teamb.charity.services.CharityService;

@RestController
@RequestMapping("/charity")
public class CharityController {

    @Autowired
    private CharityService service;

    @GetMapping("/all")
    public List<Charity> getAllCharities(){
        return service.getAllCharities();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Charity> getCharityById(@PathVariable String id){
        return service.getCharitiesByAccountId(id);
    }
}
