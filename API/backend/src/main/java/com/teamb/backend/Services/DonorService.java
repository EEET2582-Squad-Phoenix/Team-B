package com.teamb.backend.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.teamb.backend.Models.Donor;
import com.teamb.backend.Repositories.DonorRepository;

@Service
public class DonorService {
    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    public List<Donor> getAllDonors(){
        return donorRepository.findAll();
    }

    public Donor getDonorsByAccountId(String accountId){
        return donorRepository.findById(accountId).get();
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
}
