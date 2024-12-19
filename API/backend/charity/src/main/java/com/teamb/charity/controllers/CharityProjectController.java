package com.teamb.charity.controllers;

import com.teamb.charity.models.CharityProject;
import com.teamb.charity.services.CharityProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("Create")
    public ResponseEntity<CharityProject> createCharityProject(@RequestBody CharityProject newProject) {
        var result = charityProjectService.createCharityProject(newProject);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharityProject> updateCharityProject(@RequestBody charityProject updateProject) 
    {
        var result = charityProjectService.updateCharityProject(id, updateProject);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProblemDetail> deleteCharityProject(@PathVariable String id) {
        charityProjectService.deleteCharityProject(id);
        ProblemDetail deletedMsg = ProblemDetail.forStatus(HttpStatus.OK);
        deletedMsg.setTitle("Charity project deleted successfully");
        deletedMsg.setDetail(String.format("Charity project with id %s deleted successfully", id));
        return ResponseEntity.ok(deletedMsg);
    }

}
