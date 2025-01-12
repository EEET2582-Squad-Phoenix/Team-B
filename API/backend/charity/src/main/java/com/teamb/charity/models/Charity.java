package com.teamb.charity.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Charity implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private String id; // Same as Account ID

    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    
    private String displayedLogo;
    private String displayedIntroVid;

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

    //! Check naming conventions
    @Min(0)
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private Double monthlyDonation = 0.0;

    @DBRef
    @JsonIdentityReference(alwaysAsId = true)
    private Account account; // Reference to Account
}