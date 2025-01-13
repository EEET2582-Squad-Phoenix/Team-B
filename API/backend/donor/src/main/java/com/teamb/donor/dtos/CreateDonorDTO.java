package com.teamb.donor.dtos;

import com.teamb.common.models.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDonorDTO {
    private String email;
    private String password;
    private Role role;
    private Boolean emailVerified = false;

    private String avatarUrl;
    private String donorIntroVidUrl;
    private String firstName;
    private String lastName;
    private String donorAddress;
    private String language;
}
