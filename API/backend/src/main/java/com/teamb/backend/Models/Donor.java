package com.teamb.backend.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("donors")
public class Donor {
    @Id
    private String id; // Same as Account ID

    private String name;
    private String avatarUrl;
    private String introVidUrl;
    private String firstName;
    private String lastName;
    private String address;
    private String language;


    @DBRef
    private Account account; // Reference to Account
}
