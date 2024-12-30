package com.teamb.charity.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.teamb.charity.models.Charity;
import com.teamb.charity.repositories.CharityRepository;
import com.teamb.common.exception.EntityNotFound;

@Service
public class CharityService {
    @Autowired
    private CharityRepository charityRepository;

    public List<Charity> getAllCharities() {
        return charityRepository.findAll();
    }

    // Get charity by account id
    public ResponseEntity<Charity> getCharitiesByAccountId(String accountId) {
        Optional<Charity> charityOptional = charityRepository.findById(accountId);

        if (charityOptional.isPresent()) {
            return ResponseEntity.ok(charityOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // validate input charity
    private void validateInputCharity(Charity charity) {
        if (charity.getName() == null || charity.getName().isEmpty()) {
            throw new IllegalArgumentException("Charity name is required");
        }
        if (charity.getAddress() == null || charity.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Charity address is required");
        }
        if (charity.getTaxCode() == null || charity.getTaxCode().isEmpty()) {
            throw new IllegalArgumentException("Charity tax code is required");
        }
        if (charity.getType() == null) {
            throw new IllegalArgumentException("Charity type is required");
        }
        if (charity.getAccount() == null || charity.getAccount().getId() == null
                || charity.getAccount().getId().isEmpty()) {
            throw new IllegalArgumentException("Charity account is required");
        }
    }

    // Save charity
    public Charity saveCharity(Charity charity) {
        validateInputCharity(charity);
        if (charity.getId() == null || charity.getId().isEmpty()) {
            charity.setId(UUID.randomUUID().toString());
        }
        return charityRepository.save(charity);
    }

    // Update charity
    public Charity updateCharity(String id, Charity charity) {
        var existingCharity = charityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charity not found"));
        validateInputCharity(charity);
        return charityRepository.save(charity);
    }

    // Delete charity
    public void deleteCharity(String id) {
        boolean isExisted = charityRepository.existsById(id);
        if (!isExisted) {
            throw new EntityNotFound("charity Id", id);
        }
        charityRepository.deleteById(id);
    }

}
