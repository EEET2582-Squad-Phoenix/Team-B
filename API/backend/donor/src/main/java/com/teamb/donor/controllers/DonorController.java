package com.teamb.donor.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import com.teamb.donor.models.Donor;
import com.teamb.donor.services.DonorService;
import com.teamb.subscription.models.Subscription;

@RestController
@RequestMapping("/donor")
public class DonorController {

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

    // Get a donor by name
    @GetMapping("/name/{name}")
    public List<Donor> getDonorByName(@PathVariable String name) {
        return donorService.getDonorsByName(name);
    }

    // Return a donor's subscription
    // @GetMapping("/{id}/subscription")
    // public Subscription getSubscription(@PathVariable String id) {
    //     return donorService.getSubscription(id);
    // }

    // Return monthly donation for a donor
    @GetMapping("/{id}/monthlyDonation")
    public Double getMonthlyDonation(@PathVariable String id) {
        return donorService.getMonthlyDonation(id);
    }

    // Create a new donor
    // Incorrect, does not create account alongside donor
    // @PostMapping("")
    // public ResponseEntity<Donor> createDonor(@RequestBody Donor donor) {
    //     Donor createdDonor = donorService.saveDonor(donor);
    //     return ResponseEntity.ok(createdDonor);
    // }

    // Update an existing donor
    @PutMapping("/{id}")
    public ResponseEntity<Donor> updateDonor(@PathVariable String id, @RequestBody Donor donor) {
        Donor updatedDonor = donorService.updateDonor(id, donor);
        return ResponseEntity.ok(updatedDonor);
    }

    // Delete a donor by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDonor(@PathVariable String id) {
        donorService.deleteDonor(id);
        return ResponseEntity.ok("Donor deleted successfully");
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