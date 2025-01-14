package com.teamb.charity_projects.dtos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.teamb.common.models.ProjectCategoryType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateProjectDTO {

    private String name;
    private String description;
    private String thumbnailUrl;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private String country;
    private List<ProjectCategoryType> categories;
    private BigDecimal goalAmount;
    private Boolean isGlobal;
    private Date startDate; 
    private Date endDate;

}

