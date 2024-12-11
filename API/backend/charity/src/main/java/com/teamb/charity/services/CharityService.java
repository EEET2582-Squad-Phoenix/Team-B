package com.teamb.charity.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamb.charity.models.Charity;
import com.teamb.charity.repositories.CharityRepository;


@Service
public class CharityService {
    @Autowired
    private CharityRepository charityRepository;

    public List<Charity> getAllCharities(){
        return charityRepository.findAll();
    }

    public Charity getCharitiesByAccountId(String accountId){
        return charityRepository.findById(accountId).get();
    }

}
