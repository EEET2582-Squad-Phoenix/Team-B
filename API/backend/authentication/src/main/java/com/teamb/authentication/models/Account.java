package com.teamb.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.teamb.common.models.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("accounts")
public class Account{
    @Id
    private String id;
    private String email;
    private String password;
    private Role role; 
    private Boolean emailVerified;
    private Boolean adminCreated;
    private Instant createdAt;
    private Instant updatedAt;

    private String verificationToken;

}

