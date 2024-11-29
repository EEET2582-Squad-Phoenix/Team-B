package com.teamb.backend.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.backend.Models.Donor;
import com.teamb.backend.Services.DonorService;

@RestController
@RequestMapping("/donor")
public class DonorController {

    @Autowired
    private DonorService service;

    @GetMapping("/all")
    public List<Donor> getAllDonors(){
        return service.getAllDonors();
    }

    @GetMapping("/getById/{id}")
    public Donor getDonorById(@PathVariable String id){
        return service.getDonorsByAccountId(id);
    }
}
