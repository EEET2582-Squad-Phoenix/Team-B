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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    // List of continents
    // ! Make it enum later
    private List<String> continents;
    
    @NotEmpty(message = "At least one category must be selected.")
    @Size(max = 8, message = "A maximum of 8 categories can be selected.")
    private List<ProjectCategoryType> categories;

    @AssertTrue(message = "Categories must be unique.")
    private boolean isCategoriesUnique() {
        return categories != null && categories.stream().distinct().count() == categories.size();
    }

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}