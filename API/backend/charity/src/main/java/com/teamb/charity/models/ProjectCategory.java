package com.teamb.charity.models;

import com.teamb.common.models.ProjectCategoryType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("projectCategories")
public class ProjectCategory {
    private String id;

    @NotNull
    private ProjectCategoryType projectCategory;

    @DBRef
    private CharityProject charityProject;
}
