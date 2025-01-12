package com.teamb.charity_projects.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.teamb.common.models.ProjectCategoryType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateProjectDTO {

    private String id;
    private String name;
    private String thumbnailUrl;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private String description;
    private String country;
    private List<ProjectCategoryType> categories;
    private BigDecimal goalAmount;

    //! isGlobal, status, isHighlighted, fundStatus, startDate, endDate
}