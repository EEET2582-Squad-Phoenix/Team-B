package com.teamb.statistic.models;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotEmpty(message = "At least one category must be selected.")
    @Size(max = 8, message = "A maximum of 8 categories can be selected.")
    private List<ProjectCategoryType> filterCategories;

    @AssertTrue(message = "Categories must be unique.")
    private boolean isCategoriesUnique() {
        return filterCategories != null && filterCategories.stream().distinct().count() == filterCategories.size();
    }

    private List<String> filterCountries;
    //! Make it enum later
    private List<String> filterContinents;

    private Date filterStartDate;
    private Date filterEndDate;
    @AssertTrue(message = "End date must be after start date")
    private boolean isEndDateAfterStartDate() {
        return filterEndDate != null && filterStartDate != null && filterEndDate.after(filterStartDate);
    }

    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private Double value;

    private Instant createdAt;
}