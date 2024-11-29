package com.teamb.backend.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamb.backend.Models.Donor;
import com.teamb.backend.Repositories.DonorRepository;

@Service
public class DonorService {
    @Autowired
    private DonorRepository donorRepository;

    public List<Donor> getAllDonors(){
        return donorRepository.findAll();
    }

    public Donor getDonorsByAccountId(String accountId){
        return donorRepository.findById(accountId).get();
    }
}
