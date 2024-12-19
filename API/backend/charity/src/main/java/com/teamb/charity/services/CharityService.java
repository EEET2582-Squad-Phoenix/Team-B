package com.teamb.charity.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<Charity> getCharitiesByAccountId(String accountId) {
        Optional<Charity> charityOptional = charityRepository.findById(accountId);
        
        if (charityOptional.isPresent()) {
            return ResponseEntity.ok(charityOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
