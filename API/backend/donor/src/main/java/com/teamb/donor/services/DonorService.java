package com.teamb.donor.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.teamb.common.services.ImageUploadService;
import com.teamb.donor.models.Donor;
import com.teamb.donor.repositories.DonorRepository;


@Service
public class DonorService {
    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Cacheable(value = "allDonors")
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

            return ResponseEntity.ok("Avatar updated successfully: " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading avatar: " + e.getMessage());
        }
    }
}
