package com.teamb.common.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.common.services.MailService;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/notification")
public class SendNotiController {

    @Autowired
    private MailService mailService;

    @PostMapping("/sendHaltCharity")
    public ResponseEntity<String> sendHaltCharity(@RequestBody Map<String, String> request) {
        try {
            String recipientEmail = request.get("recipientEmail");
            String charityName = request.get("charityName");
            String projectName = request.get("projectName");
            String haltReason = request.get("haltReason");
            mailService.sendHaltProjectToCharity(recipientEmail, charityName, projectName, haltReason);
            return ResponseEntity.ok("Halt project email sent successfully to " + recipientEmail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/sendHaltDonor")
    public ResponseEntity<String> sendHaltDonor(@RequestBody Map<String, String> request) {
        try {
            String recipientEmail = request.get("recipientEmail");
            String charityName = request.get("charityName");
            String projectName = request.get("projectName");
            String haltReason = request.get("haltReason");
            mailService.sendHaltProjectToDonor(recipientEmail, charityName, projectName, haltReason);
            return ResponseEntity.ok("Halt project email sent successfully to " + recipientEmail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }
    
}
