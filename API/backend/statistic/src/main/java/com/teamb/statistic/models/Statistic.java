package com.teamb.statistic.models;
 
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.teamb.common.models.ProjectCategoryType;
import com.teamb.common.models.Country;
import com.teamb.common.models.Continent;
import java.math.BigDecimal;
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
    private String id;

    private List<String> userTargetIDs; // List of target IDs
    private StatisticType statisticType;

    private String filterCountry;
    private String filterContinent;
    // @Size(max = 8, message = "A maximum of 8 categories can be selected.")
    private List<String> filterCategory;

    private Date filterStartDate;
    private Date filterEndDate;
    @AssertTrue(message = "End date must be after start date")
    private boolean isEndDateAfterStartDate() {
        return filterEndDate != null && filterStartDate != null && filterEndDate.after(filterStartDate);
    }
 
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @Builder.Default
    private Double value = 0.0;


    private Instant createdAt;
}