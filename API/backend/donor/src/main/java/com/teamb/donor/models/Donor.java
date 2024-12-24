package com.teamb.donor.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.teamb.account.models.Account;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("donors")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Donor implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private String id; // Same as Account ID

    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String introVidUrl;
    private String address;
    private String language;

    @DBRef
    @JsonIdentityReference(alwaysAsId = true)
    private Account account; // Reference to Account
}