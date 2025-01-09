package com.teamb.charity.services;

import java.time.Instant;
// import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.teamb.account.models.Account;
import com.teamb.common.models.Role;
import com.teamb.charity.models.Charity;

import com.teamb.account.repositories.AccountRepository;
import com.teamb.charity.repositories.CharityRepository;
import com.teamb.common.configurations.PasswordEncoding;
import com.teamb.common.exception.EntityNotFound;

@Service
public class CharityService {
    @Autowired
    private CharityRepository charityRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoding passwordEncoding;
    

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

     // Validate input charity
     private void validateInputCharity(Charity charity, boolean validateAccount) {
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
        if (validateAccount && (charity.getAccount() == null)) {
            throw new IllegalArgumentException("Charity account is required");
        }
    }

    // Save charity
    public Charity saveCharity(Charity charity) {
        validateInputCharity(charity, true);
        if (charity.getId() == null || charity.getId().isEmpty()) {
            charity.setId(UUID.randomUUID().toString());
        }

        Account newAccount = new Account();
        newAccount.setId(charity.getId());
        newAccount.setEmail(charity.getAccount().getEmail());
        newAccount.setPassword(passwordEncoding.passwordEncoder().encode(charity.getAccount().getPassword()));
        newAccount.setRole(Role.CHARITY);
        newAccount.setEmailVerified(false);
        newAccount.setAdminCreated(false);
        newAccount.setCreatedAt(Instant.now());
        newAccount = accountRepository.save(newAccount);

        // Link the account to the charity
        charity.setAccount(newAccount);
        return charityRepository.save(charity);
    }

    // Update charity
    public Charity updateCharity(String id, Charity charity) {
        var existingCharity = charityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charity not found"));
        validateInputCharity(charity, false);

        existingCharity.setName(charity.getName());
        existingCharity.setAddress(charity.getAddress());
        existingCharity.setType(charity.getType());
        existingCharity.setTaxCode(charity.getTaxCode());
        existingCharity.setMonthlyDonation(charity.getMonthlyDonation());
        existingCharity.setIntroVidUrl(charity.getIntroVidUrl());
        existingCharity.setLogoUrl(charity.getLogoUrl());

        // update account if needed
        if (charity.getAccount() != null) {
            existingCharity.getAccount().setUpdatedAt(Instant.now());
            // existingCharity.getAccount().setEmail(charity.getAccount().getEmail());
            // existingCharity.getAccount().setPassword(passwordEncoding.passwordEncoder().encode(charity.getAccount().getPassword()));
        }
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
