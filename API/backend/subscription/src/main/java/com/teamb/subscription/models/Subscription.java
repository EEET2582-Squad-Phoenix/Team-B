package com.teamb.subscription.models;

import com.teamb.common.models.ProjectCategoryType;
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
    @DBRef
    private String donorId;

    @NotNull
    private String region; // Continent

    @NotNull
    List<ProjectCategoryType> categories;

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
}