package com.teamb.statistic.models;

import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import com.teamb.common.models.ProjectCategoryType;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("statistics")
public class Statistic {
    @Id
    private String id; // Same as Account ID

    private List<String> userTargetIDs; // List of foreign keys for multiple users
    private String userTargetID; // Single foreign key for one user
    private StatisticType statisticType;

    private String filterCountry;
    private String filterContinent;
    private ProjectCategoryType filterCategory;
    private Date filterStartDate;
    private Date filterEndDate;
    
    // 14 digits before the decimal point and up to 2 digits after the decimal point.
    @Digits(integer = 14, fraction = 2)
    private double value;

    private Instant createdAt;
}