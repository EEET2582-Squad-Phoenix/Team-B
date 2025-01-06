package com.teamb.donation.dtos;
import lombok.Data;

@Data
public class DonationDTO {
    private String id;
    private String donorId; // Donor ID reference
    private String projectId; // CharityProject ID reference
    private String creditCardId; // CreditCard ID reference
    private Double amount; // Donation amount
    private String message; // Optional message from donor
    private String status; // Status: SUCCESS, FAILED, or PENDING
    private Boolean isRecurring; // Indicates if it's recurring
    private String donationDate; // Date of donation
}
