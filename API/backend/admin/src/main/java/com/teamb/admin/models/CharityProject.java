package com.teamb.admin.models;

import com.teamb.charity.models.Charity;
import com.teamb.common.models.FundStatus;
import com.teamb.common.models.ProjectStatus;
import com.teamb.common.models.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("charityProject")
public class CharityProject {
    @Id
    private String id;
    private String name;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private String description;
    private String country;
    private BigDecimal goalAmount;
    private BigDecimal raisedAmount;
    private Region region;
    private ProjectStatus status;
    private String haltedReason;
    private boolean highlighted;
    private FundStatus fundStatus;
    private Duration duration;
    private Date endedAt;

    private Date createdAt;
    private Date updatedAt;

    @DBRef
    private Charity charity;
    @DBRef
    private Continent continent;
}
