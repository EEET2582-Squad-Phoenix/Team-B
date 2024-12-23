package com.teamb.charity.models;

import com.teamb.common.models.FundStatus;
import com.teamb.common.models.ProjectStatus;
import com.teamb.common.models.Region;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("charityProjects")
public class CharityProject {
    @Id
    private String id;
    @NotNull
    private String name;
    private List<String> imageUrls;
    private List<String> videoUrls;
    @NotNull
    private String description;
    @NotNull
    private String country;
    @NotNull
    private BigDecimal goalAmount;
    @Builder.Default
    private BigDecimal raisedAmount = BigDecimal.ZERO;
    @NotNull
    private Region region;
    @Builder.Default
    private ProjectStatus status = ProjectStatus.UNAPPROVED;
    private String haltedReason;
    private boolean highlighted;
    @Builder.Default
    private FundStatus fundStatus = FundStatus.ONGOING;
    @NotNull
    private Duration duration;
    private Date endedAt;
    private String category;

    private Date createdAt;
    private Date updatedAt;

    @DBRef
    @NotNull
    private Charity charity;
    @DBRef
    @NotNull
    private Continent continent;
}
