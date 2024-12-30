package com.teamb.account.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.teamb.common.models.Role;

import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("accounts")
public class Account{
    @Id
    private String id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Role role; 
    private Boolean emailVerified;
    private Boolean adminCreated;
    private Instant createdAt;
    private Instant updatedAt;

    private String verificationToken;

}

