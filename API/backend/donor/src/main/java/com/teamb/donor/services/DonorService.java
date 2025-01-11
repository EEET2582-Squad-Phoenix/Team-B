package com.teamb.donor.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.teamb.account.models.Account;
import com.teamb.donor.models.Donor;
import com.teamb.common.configurations.PasswordEncoding;
import com.teamb.common.models.Role;

import com.teamb.common.services.ImageUploadService;
import com.teamb.donor.repositories.DonorRepository;

import com.teamb.account.repositories.AccountRepository;

@Service
public class DonorService {
    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private PasswordEncoding passwordEncoding;

    public Account getAccount(Donor donor){
        return accountRepository.findById(donor.getId()).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    // Fetch all donors
    @Cacheable(value = "allDonors", condition = "#redisAvailable")
    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    // Fetch donor by account ID
    @Cacheable(value = "donor", condition = "#redisAvailable", key = "#id")
    public Donor getDonorsByAccountId(String id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
    }

    // Upload image for a donor
    @Caching(evict = {
        @CacheEvict(value = "donor", condition = "#redisAvailable", key = "#donorId"),
        @CacheEvict(value = "allDonors", condition = "#redisAvailable", allEntries = true)
    })
    public ResponseEntity<?> uploadImage(String donorId, MultipartFile file, int height, int width) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded");
        }

        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        try {
            String imageUrl = imageUploadService.uploadImage(file, height, width);
            donor.setAvatarUrl(imageUrl);
            donorRepository.save(donor);

            return ResponseEntity.ok("Avatar updated successfully: " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading avatar: " + e.getMessage());
        }
    }

    // Validate donor input
    private void validateInputDonor(Donor donor) {
        if (donor == null) {
            throw new IllegalArgumentException("Donor object cannot be null");
        }
        if (donor.getFirstName() == null || donor.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("Donor first name is required");
        }
        if (donor.getLastName() == null || donor.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Donor last name is required");
        }
        if (donor.getLanguage() == null || donor.getLanguage().isEmpty()) {
            throw new IllegalArgumentException("Donor language is required");
        }
        if (getAccount(donor) == null) {
            throw new IllegalArgumentException("Donor account cannot be null");
        }
    }

    // Save donor and create associated account
    // This is incorrect
    // @CachePut(value = "donor", condition = "#redisAvailable", key = "#result.id")
    // @CacheEvict(value = "allDonors", condition = "#redisAvailable", allEntries = true)
    // public Donor saveDonor(Donor donor) {
    //     validateInputDonor(donor);

    //     if (donor.getId() == null || donor.getId().isEmpty()) {
    //         donor.setId(UUID.randomUUID().toString());
    //     }

    //     // Create and save associated account
    //     Account account = new Account();
    //     account.setId(donor.getId()); // Ensure donor ID matches account ID
    //     account.setEmail(donor.getAccount().getEmail());
    //     account.setPassword(passwordEncoding.passwordEncoder().encode(donor.getAccount().getPassword())); 
    //     account.setRole(Role.DONOR);
    //     account.setEmailVerified(false);
    //     account.setAdminCreated(false);
    //     account.setCreatedAt(Instant.now());
    //     account = accountRepository.save(account);

    //     // Link the account to the donor
    //     donor.setAccount(account);

    //     return donorRepository.save(donor);
    // }

    // Update donor
    @CachePut(value = "donor", condition = "#redisAvailable", key = "#id")
    @CacheEvict(value = "allDonors", condition = "#redisAvailable", allEntries = true)
    public Donor updateDonor(String id, Donor donor) {
        Donor existingDonor = donorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Donor not found"));

        validateInputDonor(donor);

        existingDonor.setFirstName(donor.getFirstName());
        existingDonor.setLastName(donor.getLastName());
        existingDonor.setAvatarUrl(donor.getAvatarUrl());
        existingDonor.setIntroVidUrl(donor.getIntroVidUrl());
        existingDonor.setAddress(donor.getAddress());
        existingDonor.setLanguage(donor.getLanguage());
        existingDonor.setMonthlyDonation(donor.getMonthlyDonation());
        existingDonor.setSubscriptions(donor.getSubscriptions());
        existingDonor.setStripeCustomerId(donor.getStripeCustomerId());


        // Update account fields if needed
        if (getAccount(existingDonor) != null) {
            getAccount(existingDonor).setUpdatedAt(Instant.now());
            // existingDonor.getAccount().setEmail(donor.getAccount().getEmail());
            // existingDonor.getAccount().setPassword(passwordEncoding.passwordEncoder().encode(donor.getAccount().getPassword())); 
        }

        return donorRepository.save(existingDonor);
    }

    // Delete donor
    @Caching(evict = {
        @CacheEvict(value = "donor", condition = "#redisAvailable", key = "#id"),
        @CacheEvict(value = "allDonors", condition = "#redisAvailable", allEntries = true)
    })
    public void deleteDonor(String id) {
        if (!donorRepository.existsById(id)) {
            throw new IllegalArgumentException("Donor not found");
        }
        donorRepository.deleteById(id);
    }
}