package com.teamb.donor.services;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.teamb.common.models.Role;
import com.teamb.common.services.ImageUploadService;
import com.teamb.donor.models.Donor;
import com.teamb.donor.repositories.DonorRepository;


@Service
public class DonorService {
    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    public List<Donor> getAllDonors(){
        return donorRepository.findAll();
    }

    public ResponseEntity<Donor> getDonorsByAccountId(String accountId) {
        Optional<Donor> donorOptional = donorRepository.findById(accountId);
        
        if (donorOptional.isPresent()) {
            return ResponseEntity.ok(donorOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    

    public ResponseEntity<?> uploadImage(String donorId, MultipartFile file, int height, int width) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(400).body("No file uploaded");
            }

            String imageUrl = imageUploadService.uploadImage(file, height, width);

            Donor donor = donorRepository.findById(donorId).orElseThrow(() -> new RuntimeException("Donor not found"));
            donor.setAvatarUrl(imageUrl);
            donorRepository.save(donor);

            return ResponseEntity.ok("Avatar updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading avatar: " + e.getMessage());
        }
    }

    // validate input donor
    private void validateInputDonor(Donor donor) {
        if (donor.getFirstName() == null || donor.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("Donor first name is required");
        }
        if (donor.getLastName() == null || donor.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Donor last name is required");
        }
        if (donor.getAddress() == null || donor.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Donor address is required");
        }
        if (donor.getLanguage() == null || donor.getLanguage().isEmpty()) {
            throw new IllegalArgumentException("Donor language is required");
        }
        if (donor.getAccount().getEmail() == null || donor.getAccount().getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (donor.getAccount().getPassword() == null || donor.getAccount().getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    // Save donor
    public Donor saveDonor(Donor donor) {
        validateInputDonor(donor);
        if (donor.getId() == null || donor.getId().isEmpty()) {
            donor.setId(UUID.randomUUID().toString());
        }
        donor.getAccount().setRole(Role.DONOR);

        Instant now = Instant.now();
        if(donor.getAccount().getCreatedAt() == null) {
            donor.getAccount().setCreatedAt(now);
        }
        donor.getAccount().setEmailVerified(false);
        donor.getAccount().setAdminCreated(true);

        return donorRepository.save(donor);
    }

    // Update donor
    public Donor updateDonor(String id, Donor donor) {
        var existingDonor = donorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Donor not found"));
        validateInputDonor(donor);
        donor.getAccount().setUpdatedAt((Instant.now()));
        return donorRepository.save(donor);
    }

    // Delete donor
    public void deleteDonor(String id) {
        boolean isExisted = donorRepository.existsById(id);
        if (!isExisted) {
            throw new IllegalArgumentException("Donor not found");
        }
        donorRepository.deleteById(id);
    }

    
}
