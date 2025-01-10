package com.teamb.account.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

// import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.teamb.common.models.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("accounts")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Account implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Email
    @Indexed(unique = true)
    private String email;

    @NotNull
    //! Needs constraints
    private String password;

    @NotNull
    private Role role;
    private Boolean emailVerified = false;
    private Boolean adminCreated = false;

    private Instant createdAt;
    private Instant updatedAt;

    private String verificationToken;
}