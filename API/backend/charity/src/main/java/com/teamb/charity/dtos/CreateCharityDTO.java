package com.teamb.charity.dtos;

import java.util.List;

import com.teamb.common.models.CharityType;
import com.teamb.common.models.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCharityDTO {
    // Common Account fields
    private String email;
    private String password;
    private Role role;
    private Boolean emailVerified = false;

    // Charity-specific fields
    private String name;
    private String displayedLogo;
    private String displayedIntroVid;
    private List<String> logoUrl;
    private List<String> introVidUrl;
    private String address;
    private String taxCode;
    private CharityType charityType;
}


