package com.teamb.admin.models;

import com.teamb.common.models.ProjectCategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("projectCategory")
public class ProjectCategory {
    private String id;

    private ProjectCategoryType projectCategory;

    private CharityProject charityProject;
}
