package com.teamb.admin.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teamb.common.models.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude
public class ProjectStatusUpdateResponse {
    private String id;
    private String name;
    private ProjectStatus status;
}
