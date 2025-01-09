package com.teamb.subscription.models;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("subscriptions")
public class Subscription {

    @Id
    private String id;

    @NotNull
    @DocumentReference(lazy = true)
    private String donorId;

    @NotNull
    private Region region;

    @NotNull
    private Category category;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum Region {
        AFRICA,
        EUROPE,
        ASIA,
        AMERICA
    }

    public enum Category {
        EDUCATION,
        HEALTH,
        RELIGION,
        ENVIRONMENTAL,
        HOUSING,
        OTHER
    }
}