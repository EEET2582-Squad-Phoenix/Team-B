package com.teamb.backend.Models;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Document("accounts")
public class Account {
    @Id
    private String id;
    private String email;
    private String password;
    private Enum role; //Remember to create the Enum 
    private Boolean emailVerified;
    private Boolean adminCreated;
    private Date createdAt;
    private Date updatedAt;
}
