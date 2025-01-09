package com.teamb.statistic.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
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

import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("statistics")
public class Statistic {
    @Id
    private String id; // Same as Account ID

    private String filterCountry;
    private String filterContinent;
    private Instant createdAt;

    // @NotNull
    // @Size(min = 1, max = 255)
    // private String name;
    
    // private List<String> logoUrl;
    // private List<String> introVidUrl;


    // @NotNull
    // @Size(min = 1, max = 255)
    // private String address;

    // @NotNull
    // @Pattern(regexp = "^[A-Za-z0-9]{5,10}$", message = "Invalid tax code")
    // private String taxCode;

    // @NotNull
    // @Field("organizationType")
    // private CharityType type;

    // @Min(0)
    // private Double monthlyDonation = 0.0;

    // @DBRef
    // private Account account; // Reference to Account
    // @DBRef
    // private Charity charity;

}

