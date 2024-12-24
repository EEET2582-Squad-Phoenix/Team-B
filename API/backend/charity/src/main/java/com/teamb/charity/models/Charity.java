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


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("charities")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Charity implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private String id; // Same as Account ID

    private String name;
    private List<String> logoUrl;
    private List<String> introVidUrl;
    private String address;
    private String taxCode;
    private CharityType type;

    @DBRef
    @JsonIdentityReference(alwaysAsId = true)
    private Account account; // Reference to Account
}

