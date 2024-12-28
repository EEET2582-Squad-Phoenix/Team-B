package com.teamb.admin.controllers;

import com.teamb.charity.models.CharityProject;
import com.teamb.charity.services.CharityProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final CharityProjectService charityProjectService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/highlight/global/{id}")
    public ResponseEntity<CharityProject> highlightGlobalProject(@PathVariable("id") String projectId) {
        var project = charityProjectService.highlightGlobalProject(projectId);
        return ResponseEntity.ok(project);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/highlight/regional/{id}")
    public ResponseEntity<CharityProject> highlightRegionalProject(@PathVariable("id") String projectId) {
        var project = charityProjectService.highlightRegionalProject(projectId);
        return ResponseEntity.ok(project);
    }
}