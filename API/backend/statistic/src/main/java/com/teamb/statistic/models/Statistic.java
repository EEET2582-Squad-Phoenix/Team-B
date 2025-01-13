package com.teamb.statistic.models;
 
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.teamb.common.models.ProjectCategoryType;
 
import java.time.Instant;
import java.util.Date;
import java.util.List;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("statistics")
public class Statistic {
    @Id
    private String id; // Same as Account ID
 
    private List<String> userTargetIDs; // List of foreign keys for multiple users
    private StatisticType statisticType;
 
    private String filterCountry;
    private String filterContinent;
    private List <String> filterCategory;
    private Date filterStartDate;
    private Date filterEndDate;
 
    // 14 digits before the decimal point and up to 2 digits after the decimal
    // point.
    @Digits(integer = 14, fraction = 2)
    private Double value;
 
    private Instant createdAt;
}