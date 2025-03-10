package com.teamb.admin.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.teamb.donor.dtos.CreateDonorDTO;
import com.teamb.donor.models.Donor;
import com.teamb.donor.services.DonorService;

@RestController
@RequestMapping("/admin/donor")
public class AdminDonorController {

    @Autowired
    private DonorService donorService;

    // Get all donors
    @GetMapping("/all")
    public List<Donor> getAllDonors() {
        return donorService.getAllDonors();
    }

    // Get a donor by ID
    @GetMapping("/{id}")
    public ResponseEntity<Donor> getDonorById(@PathVariable String id) {
        Donor donor = donorService.getDonorsByAccountId(id);
        return ResponseEntity.ok(donor);
    }

    // Create a new donor
    @PostMapping("")
    public ResponseEntity<Donor> createDonor(@RequestBody CreateDonorDTO donor) {
        Donor createdDonor = donorService.saveDonor(donor);
        return ResponseEntity.ok(createdDonor);
    }

    // Update an existing donor
    @PutMapping("/{id}")
    public ResponseEntity<Donor> updateDonor(@PathVariable String id, @RequestBody Donor donor) {
        Donor updatedDonor = donorService.updateDonor(id, donor);
        return ResponseEntity.ok(updatedDonor);
    }

    // Delete a donor by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ProblemDetail> deleteDonor(@PathVariable String id) {
        donorService.deleteDonor(id);
        ProblemDetail deletedMsg = ProblemDetail.forStatus(HttpStatus.OK);
        deletedMsg.setTitle("Donor deleted successfully");
        deletedMsg.setDetail(String.format("Donor with id %s deleted successfully", id));
        return ResponseEntity.ok(deletedMsg);
    }

    // Upload avatar for donor (300x300)
    @PostMapping("/{id}/avatar/300")
    public ResponseEntity<?> uploadAvatar(@PathVariable String id, @RequestParam("image") MultipartFile file) {
        return donorService.uploadImage(id, file, 300, 300);
    }

    // Upload thumbnail for donor (100x100)
    @PostMapping("/{id}/avatar/100")
    public ResponseEntity<?> uploadThumbnail(@PathVariable String id, @RequestParam("image") MultipartFile file) {
        return donorService.uploadImage(id, file, 100, 100);
    }
}