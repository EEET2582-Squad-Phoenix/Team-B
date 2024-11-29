package com.teamb.backend.Models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Registration {
    // Common Account fields
    private String email;
    private String password;
    private Role role;
    private Boolean emailVerified = false;

    // Charity-specific fields
    private String name;
    private List<String> logoUrl;
    private List<String> introVidUrl;
    private String address;
    private String taxCode;
    private Type charityType;

    // Donor-specific fields
    private String avatarUrl;
    private String donorIntroVidUrl;
    private String firstName;
    private String lastName;
    private String donorAddress;
    private String language;
}
