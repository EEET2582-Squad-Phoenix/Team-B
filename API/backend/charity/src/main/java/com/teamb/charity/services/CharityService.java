package com.teamb.charity.services;

import java.time.Instant;
// import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.teamb.account.models.Account;
import com.teamb.common.models.Role;
import com.teamb.common.services.MailService;
import com.teamb.common.models.CharityType;
import com.teamb.charity.dtos.CreateCharityDTO;
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
    @Autowired
    private MailService mailService;

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

    // Fetch charity account by id
    public Account getAccount(Charity charity){
        return accountRepository.findById(charity.getId()).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    // Fetch all charities
    @Cacheable(value = "allCharities", condition = "#redisAvailable")
    public List<Charity> getAllCharities() {
        return charityRepository.findAll();
    }

    // Fetch charity info by id
    @Cacheable(value = "charity", condition = "#redisAvailable", key = "#accountId")
    public Charity getCharityByAccountId(String accountId) {
        return charityRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Charity not found"));
    }

    // Fetch charity by name
    @Cacheable(value = "charity", condition = "#redisAvailable", key = "#name")
    public Charity getCharityByName(String name) {
        return charityRepository.findByName(name);
    }

    // Fetch charities by list of types;
    @Cacheable(value = "charity", condition = "#redisAvailable", key = "#type")
    public List<Charity> getCharitiesByTypes(List<CharityType> charityTypes) {
        return charityRepository.findByTypeIn(charityTypes);
    }

    // Create charity
    @CachePut(value = "charity", condition = "#redisAvailable", key = "#result.id")
    @CacheEvict(value = "allCharities", condition = "#redisAvailable", allEntries = true)
    public Charity saveCharity(CreateCharityDTO charity) {
        if (accountRepository.findByEmail(charity.getEmail()) != null) {
            throw new IllegalArgumentException("Email already taken");
        }
    
        if (charity.getName() == null || charity.getName().isEmpty() ||
        charity.getAddress() == null || charity.getAddress().isEmpty() ||
        charity.getTaxCode() == null || charity.getTaxCode().isEmpty() || charity.getEmail().isEmpty() || charity.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Missing required fields for Charity");
        } else {
            Account newAccount = new Account();
            newAccount.setEmail(charity.getEmail());
            newAccount.setPassword(passwordEncoding.passwordEncoder().encode(charity.getPassword()));
            newAccount.setRole(Role.CHARITY);
            newAccount.setEmailVerified(charity.getEmailVerified());
            newAccount.setAdminCreated(true);
            newAccount.setCreatedAt(Instant.now());
            accountRepository.save(newAccount);
            // Generate email verification token
            String verificationToken = UUID.randomUUID().toString();

            // Save the token to the account
            newAccount.setVerificationToken(verificationToken);
            accountRepository.save(newAccount);

            // Send the verification email
            mailService.sendVerificationEmail(charity.getEmail(), verificationToken);

            Charity newCharity = new Charity();
            newCharity.setId(newAccount.getId());
            newCharity.setName(charity.getName());
            newCharity.setDisplayedLogo(charity.getDisplayedLogo());
            newCharity.setDisplayedIntroVid(charity.getDisplayedIntroVid());
            newCharity.setLogoUrl(charity.getLogoUrl());
            newCharity.setIntroVidUrl(charity.getIntroVidUrl());
            newCharity.setAddress(charity.getAddress());
            newCharity.setTaxCode(charity.getTaxCode());
            newCharity.setType(charity.getCharityType());
            charityRepository.save(newCharity);

            return charityRepository.save(newCharity);
        }
    }

    // Update charity
    @CachePut(value = "charity", condition = "#redisAvailable", key = "#result.id")
    @CacheEvict(value = "allCharities", condition = "#redisAvailable", allEntries = true)
    public Charity updateCharity(String id, Charity charity) {
        var existingCharity = charityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charity not found"));
        validateInputCharity(charity, false);

        existingCharity.setName(charity.getName());
        existingCharity.setAddress(charity.getAddress());
        existingCharity.setType(charity.getType());
        existingCharity.setTaxCode(charity.getTaxCode());
        existingCharity.setMonthlyDonation(charity.getMonthlyDonation());
        existingCharity.setDisplayedLogo(charity.getDisplayedLogo());
        existingCharity.setDisplayedIntroVid(charity.getDisplayedIntroVid());
        existingCharity.setIntroVidUrl(charity.getIntroVidUrl());
        existingCharity.setLogoUrl(charity.getLogoUrl());
        existingCharity.setDisplayedIntroVid(charity.getDisplayedIntroVid());
        existingCharity.setDisplayedLogo(charity.getDisplayedLogo());

        // update account if needed
        if (getAccount(existingCharity) != null) {
            getAccount(existingCharity).setUpdatedAt(Instant.now());
            // existingCharity.getAccount().setEmail(charity.getAccount().getEmail());
            // existingCharity.getAccount().setPassword(passwordEncoding.passwordEncoder().encode(charity.getAccount().getPassword()));
        }
        return charityRepository.save(charity);
    }

    // Delete charity
    @Caching(evict = {
        @CacheEvict(value = "charity", condition = "#redisAvailable", key = "#id"),
        @CacheEvict(value = "allCharities", condition = "#redisAvailable", allEntries = true)
    })
    public void deleteCharity(String id) {
        boolean isExisted = charityRepository.existsById(id) && accountRepository.existsById(id);
        if (!isExisted) {
            throw new EntityNotFound("charity Id", id);
        }
        accountRepository.deleteById(id);
        charityRepository.deleteById(id);
    }

}
