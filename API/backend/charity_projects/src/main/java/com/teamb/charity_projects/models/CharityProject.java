package com.teamb.charity_projects.models;

import com.mongodb.lang.Nullable;
import com.teamb.charity.models.Charity;
import com.teamb.common.models.FundStatus;
import com.teamb.common.models.ProjectStatus;
import com.teamb.common.models.ProjectCategoryType;
import com.teamb.donor.models.Donor;
// import com.teamb.common.models.Region;

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

    //! Recommendation: Make halt as a different entity including 2 halted reasons (for charity and for donors), a timestamp
    // private String haltedReason;

    private boolean highlighted;

    //! Optional: Get rid of fundStatus as you can compare raisedAmount and goalAmount to determine the status
    @Builder.Default
    private FundStatus fundStatus = FundStatus.ONGOING;

    @NotNull
    private Date endedAt;
    private Date createdAt; // = startAt
    private Date updatedAt;

    @NotEmpty(message = "At least one category must be selected.")
    private List<ProjectCategoryType> categories;

    @Nullable
    private String stripeProductId;

    @DBRef
    @NotNull
    private Charity charity;

    @DBRef
    private List<Donor> donors;

    // ! Consider getting rid of this
    // @DBRef
    @NotNull
    private String continent;
}