package com.teamb.charity.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.teamb.account.models.Account;
import com.teamb.common.models.CharityType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Digits;

// import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("charities")
public class Charity {
    @Id
    private String id; // Same as Account ID

    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    
    //! Charity only has 1 logo & 1 intro video. Having other images & videos to display on the profile page is recommended.
    private List<String> logoUrl;
    private List<String> introVidUrl;

    @NotNull
    @Size(min = 1, max = 255)
    private String address;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9]{5,10}$", message = "Invalid tax code")
    private String taxCode;

    @NotNull
    @Field("organizationType")
    private CharityType type;

    @Min(0)
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private Double monthlyDonation = 0.0;

    @DBRef
    private Account account; // Reference to Account
}

