package com.teamb.donor.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;

import com.teamb.account.models.Account;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("donors")
public class Donor {
    @Id
    private String id; // Same as Account ID

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
    private String avatarUrl;
    private String introVidUrl;
    private String address;
    @NotNull
    private String language;


    @DBRef
    private Account account; // Reference to Account
}
