package com.teamb.donation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.donation.dtos.DonationDTO;
import com.teamb.donation.services.DonationService;

import java.util.List;

@RestController
@RequestMapping("donations")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping
    public List<DonationDTO> getDonations() {
        return donationService.getAllDonations();
    }
}
