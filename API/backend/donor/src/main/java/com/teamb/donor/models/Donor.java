package com.teamb.donor.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.teamb.account.models.Account;
import com.teamb.subscription.models.Subscription;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("donors")
public class Donor {
    @Id
    private String id; // Same as Account ID

    @NotNull
    @Size(min = 1, message = "First name is required")
    private String firstName;

    @NotNull
    @Size(min = 1, message = "Last name is required")
    private String lastName;
    private String avatarUrl;
    private String introVidUrl;
    private String address;

    @NotNull
    @Pattern(regexp = "^[a-z]{2}$", message = "Invalid language code")
    private String language = "en";

    @Min(0)
    private Double monthlyDonation = 0.0;

    @DocumentReference(lazy = true)
    private List<Subscription> subscriptions = new ArrayList<>();

    @DBRef
    private Account account; // Reference to Account
}
