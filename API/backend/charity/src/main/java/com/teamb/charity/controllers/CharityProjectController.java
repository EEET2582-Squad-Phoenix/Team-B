package com.teamb.charity.controllers;

import com.teamb.charity.models.CharityProject;
import com.teamb.charity.services.CharityProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/charity/projects")
public class CharityProjectController {

    private final CharityProjectService charityProjectService;

    @GetMapping
    public ResponseEntity<List<CharityProject>> getCharityProjects(@RequestParam(required = false) String name) {
        List<CharityProject> result = charityProjectService.findCharityProjects(name);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<CharityProject> getCharityProject(@PathVariable String projectId) {
        var project = charityProjectService.findCharityProjectById(projectId);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/byContinent/{continentId}")
    public ResponseEntity<List<CharityProject>> getCharityProjectsByContinent(@PathVariable String continentId) {
        var result = charityProjectService.findCharityProjectsByContinent(continentId);
        return ResponseEntity.ok(result);
    }

}
