package com.teamb.charity_projects.models;

import com.mongodb.lang.Nullable;
import com.teamb.charity.models.Charity;
import com.teamb.common.models.FundStatus;
import com.teamb.common.models.ProjectStatus;
import com.teamb.common.models.ProjectCategoryType;
import com.teamb.donor.models.Donor;
// import com.teamb.common.models.Region;
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
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
// import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
// import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("charityprojects")
public class CharityProject {
    @Id
    private String id;

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    private String thumbnailUrl;
    @Size(max = 15)
    private List<String> imageUrls;
    @Size(max = 4)
    private List<String> videoUrls;

    @NotNull
    @Size(min = 1, max = 255)
    private String description;
    @NotNull
    @Size(min = 1, max = 100)
    private String country;

    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @NotNull
    private BigDecimal goalAmount;
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @Builder.Default
    private BigDecimal raisedAmount = BigDecimal.ZERO;

    private boolean isGlobal;
    @Builder.Default
    private ProjectStatus status = ProjectStatus.UNAPPROVED;

    private boolean isHighlighted;

    //! Optional: Get rid of fundStatus as you can compare raisedAmount and goalAmount to determine the status
    @Builder.Default
    private FundStatus fundStatus = FundStatus.ONGOING;

    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;
    @AssertTrue(message = "End date must be after start date")
    private boolean isEndDateAfterStartDate() {
        return endDate != null && startDate != null && endDate.after(startDate);
    }

    private Date createdAt;
    private Date updatedAt;

    @NotEmpty(message = "At least one category must be selected.")
    @Size(max = 8, message = "A maximum of 8 categories can be selected.")
    private List<ProjectCategoryType> categories;

    @AssertTrue(message = "Categories must be unique.")
    private boolean isCategoriesUnique() {
        return categories != null && categories.stream().distinct().count() == categories.size();
    }

    @Nullable
    private String stripeProductId;

    //! charityID or charity
    @DBRef
    @NotNull
    private Charity charity;

    @DBRef
    private List<Donor> donorList;

    //! Rerturn Donor list (email, role, id)
    // Return emails and roles of donors
    public List<Object> getDonorEmailsAndRoles() {
        return donorList.stream()
                .map(donor -> {
                    return new Object() {
                        public final String email = donor.getAccount().getEmail();
                        public final String role = donor.getAccount().getRole().toString();
                    };
                })
                .collect(Collectors.toList());
    }

    @NotNull
    private String continent;
}