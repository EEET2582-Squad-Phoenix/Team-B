package com.teamb.charity.controllers;

import com.teamb.charity.models.CharityProject;
import com.teamb.charity.response.ProjectStatusUpdateResponse;
import com.teamb.charity.services.CharityProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/charity/projects")
public class CharityProjectController {

    private final CharityProjectService charityProjectService;
    private static final Logger logger = LoggerFactory.getLogger(CharityProjectController.class);

    private final ModelMapper modelMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<ProjectStatusUpdateResponse> approveCharityProject(@PathVariable("id") String projectId) {
        var project = charityProjectService.approveCharityProject(projectId);
        return ResponseEntity.ok(modelMapper.map(project, ProjectStatusUpdateResponse.class));
    }

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
        logger.info("API called: GET /charityProjects/search with category: {}", continentId);
        var result = charityProjectService.findCharityProjectsByContinent(continentId);
        System.out.print(continentId);
        return ResponseEntity.ok(result);
    }

//    @GetMapping("/byCategory/{categoryName}")
//    public ResponseEntity<List<CharityProject>> getCharityProjectByCategory(@PathVariable String categoryName){
//        System.out.println("here is "+ categoryName);
//        var result = charityProjectService.searchCharityProjectByCategory(categoryName);
////        System.out.println("here is "+ categoryName);
//        return ResponseEntity.ok(result);
//    }


    @GetMapping("/byCategory")
    public ResponseEntity<List<CharityProject>> getCharityProjectByCategory(@RequestParam(required = false) String categoryName){
        System.out.println("here is "+ categoryName);

//        logger.info("API called: GET /charityProjects/search with category: {}", categoryName);
        var result = charityProjectService.searchCharityProjectByCategory(categoryName);
        return ResponseEntity.ok(result);
    }
    @PostMapping("Create")
    public ResponseEntity<CharityProject> createCharityProject(@RequestBody CharityProject newProject) {
        var result = charityProjectService.saveCharityProject(newProject);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharityProject> updateCharityProject(@RequestBody CharityProject updateProject, @PathVariable String id)
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
