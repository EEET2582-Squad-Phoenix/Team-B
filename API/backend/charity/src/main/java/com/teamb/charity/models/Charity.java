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


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("charities")
public class Charity {
    @Id
    private String id; // Same as Account ID

    private String name;
    private List<String> logoUrl;
    private List<String> introVidUrl;
    private String address;
    private String taxCode;
    private CharityType type;


    @DBRef
    private Account account; // Reference to Account
}

