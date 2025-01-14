package com.teamb.donor.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
import com.teamb.donor.dtos.CreateDonorDTO;
import com.teamb.donor.models.Donor;
import com.teamb.common.configurations.PasswordEncoding;
import com.teamb.common.models.Role;

import com.teamb.common.services.ImageUploadService;
import com.teamb.common.services.MailService;
import com.teamb.donor.repositories.DonorRepository;
import com.teamb.subscription.models.Subscription;
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

    @Autowired
    private MailService mailService;

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
    }

    // Fetch account by donor
    public Account getAccount(Donor donor){
        return accountRepository.findById(donor.getId()).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    // Fetch all donors
    @Cacheable(value = "allDonors", condition = "@redisAvailability.isRedisAvailable()")
    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    // Fetch donor by account ID
    @Cacheable(value = "donor", condition = "@redisAvailability.isRedisAvailable()", key = "#id")
    public Donor getDonorsByAccountId(String id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
    }

    // Fetch donor by name (either last or first name)
    @Cacheable(value = "donor", condition = "@redisAvailability.isRedisAvailable()", key = "#name")
    public List<Donor> getDonorsByName(String name) {
        return donorRepository.findByFirstNameOrLastName(name);
    }

    // Return donor's subscription
    // @Cacheable(value = "donor", condition = "#redisAvailable", key = "#id")
    // public Subscription getSubscription(String id) {
    //     return donorRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("Donor not found"))
    //             .getSubscription();
    // }

    // Return donation for a donor
    @Cacheable(value = "donor", condition = "@redisAvailability.isRedisAvailable()", key = "#id")
    public Double getMonthlyDonation(String id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donor not found"))
                .getMonthlyDonation();
    }

    // Return donor account's email and role by donor ID
    @Cacheable(value = "donor", condition = "#redisAvailable", key = "#id")
    public ResponseEntity<?> getDonorEmailAndRole(String id) {
        Account account = getAccount(getDonorsByAccountId(id));
        return ResponseEntity.ok().body(new Object() {
            public final String email = account.getEmail();
            public final Role role = account.getRole();
        });
    }

    //! Return donor's email
    // @Cacheable(value = "donorEmail", condition = "@redisAvailability.isRedisAvailable()", key = "#donor.id")
    // public String getEmail(Donor donor) {
    //     return getAccount(donor).getEmail();
    // }

    //! Return donor's role
    // @Cacheable(value = "donor", condition = "@redisAvailability.isRedisAvailable()", key = "#donor.id")
    // public Role getRole(Donor donor) {
    //     return getAccount(donor).getRole();
    // }

    // Upload image for a donor
    @Caching(evict = {
        @CacheEvict(value = "donor", condition = "@redisAvailability.isRedisAvailable()", key = "#donorId"),
        @CacheEvict(value = "allDonors", condition = "@redisAvailability.isRedisAvailable()", allEntries = true)
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

    // Save donor and create associated account
    @CachePut(value = "donor", condition = "@redisAvailability.isRedisAvailable()", key = "#result.id")
    @CacheEvict(value = "allDonors", condition = "@redisAvailability.isRedisAvailable()", allEntries = true)
    public Donor saveDonor(CreateDonorDTO donor) {
        if (accountRepository.findByEmail(donor.getEmail()) != null) {
            throw new IllegalArgumentException("Email already taken");
        }
    
        if (donor.getFirstName() == null || donor.getFirstName().isEmpty() ||
            donor.getLastName() == null || donor.getLastName().isEmpty()) {
                throw new IllegalArgumentException("Missing required fields for Charity");
        }else{
            Account newAccount = new Account();
            newAccount.setEmail(donor.getEmail());
            newAccount.setPassword(passwordEncoding.passwordEncoder().encode(donor.getPassword()));
            newAccount.setRole(Role.DONOR);
            newAccount.setEmailVerified(donor.getEmailVerified());
            newAccount.setAdminCreated(true);
            newAccount.setCreatedAt(Instant.now());
            accountRepository.save(newAccount);
            // Generate email verification token
            String verificationToken = UUID.randomUUID().toString();

            // Save the token to the account
            newAccount.setVerificationToken(verificationToken);
            accountRepository.save(newAccount);

            // Send the verification email
            mailService.sendVerificationEmail(donor.getEmail(), verificationToken);

            Donor newDonor = new Donor();
            newDonor.setId(newAccount.getId());
            newDonor.setAvatarUrl(donor.getAvatarUrl());
            newDonor.setIntroVidUrl(donor.getDonorIntroVidUrl());
            newDonor.setFirstName(donor.getFirstName());
            newDonor.setLastName(donor.getLastName());
            newDonor.setAddress(donor.getDonorAddress());
            return donorRepository.save(newDonor);
        }
    }

    // Update donor
    @CachePut(value = "donor", condition = "@redisAvailability.isRedisAvailable()", key = "#id")
    @CacheEvict(value = "allDonors", condition = "@redisAvailability.isRedisAvailable()", allEntries = true)
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
        // existingDonor.setSubscription(donor.getSubscription());
        existingDonor.setStripeCustomerId(donor.getStripeCustomerId());

        Account updatedAccount = getAccount(existingDonor);
        // Update account fields if needed
        if (updatedAccount != null) {
            // Update account email
            if (!updatedAccount.getEmail().equals(updatedAccount.getEmail())) {
                updatedAccount.setEmail(updatedAccount.getEmail());
            }
            // Update account password
            if (!updatedAccount.getPassword().equals(updatedAccount.getPassword())) {
                updatedAccount.setPassword(passwordEncoding.passwordEncoder().encode(updatedAccount.getPassword()));
            }
            updatedAccount.setUpdatedAt(Instant.now());
            accountRepository.save(updatedAccount);
        }

        return donorRepository.save(existingDonor);
    }

    // Delete donor
    @Caching(evict = {
        @CacheEvict(value = "donor", condition = "@redisAvailability.isRedisAvailable()", key = "#id"),
        @CacheEvict(value = "allDonors", condition = "@redisAvailability.isRedisAvailable()", allEntries = true)
    })
    public void deleteDonor(String id) {
        boolean ifExist = donorRepository.existsById(id) && accountRepository.existsById(id);
        if (!ifExist) {
            throw new IllegalArgumentException("Donor not found");
        }
        donorRepository.deleteById(id);
        accountRepository.deleteById(id);
    }
}