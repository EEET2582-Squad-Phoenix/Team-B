package com.teamb.admin.controllers;

import com.teamb.admin.api.response.ProjectStatusUpdateResponse;
import com.teamb.admin.services.CharityProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/charity/projects")
@RequiredArgsConstructor
public class CharityProjectController {

    private final CharityProjectService projectService;

    @PostMapping("/approve/{id}")
    public ResponseEntity<ProjectStatusUpdateResponse> approveCharityProject(@PathVariable("id") String projectId) {
        return ResponseEntity.ok(projectService.approveCharityProject(projectId));
    }

}
