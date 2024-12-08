package com.teamb.backend.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.teamb.backend.Models.Donor;
import com.teamb.backend.Services.DonorService;

@RestController
@RequestMapping("/donor")
public class DonorController {

    @Autowired
    private DonorService service;


    @GetMapping("/all")
    public List<Donor> getAllDonors(){
        return service.getAllDonors();
    }

    @GetMapping("/getById/{id}")
    public Donor getDonorById(@PathVariable String id){
        return service.getDonorsByAccountId(id);
    }

    @PostMapping("/{id}/avatar/300")
    public ResponseEntity<?> uploadAvatar(@PathVariable String id, @RequestParam("image") MultipartFile file) {
        return service.uploadImage(id, file, 300, 300);
    }

    @PostMapping("/{id}/avatar/100")
    public ResponseEntity<?> uploadThumbnail(@PathVariable String id, @RequestParam("image") MultipartFile file) {
        return service.uploadImage(id, file, 100, 100);
    }
    
}
