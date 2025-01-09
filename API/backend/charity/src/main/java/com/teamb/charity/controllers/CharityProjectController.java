package com.teamb.charity.controllers;

import com.teamb.charity.models.CharityProject;
import com.teamb.charity.response.ProjectStatusUpdateResponse;
import com.teamb.charity.services.CharityProjectService;
import com.teamb.common.exception.EntityNotFound;
import com.teamb.common.models.ProjectStatus;
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

    // Find charity project by id
    //! Needs checking: @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{projectId}")
    public ResponseEntity<CharityProject> getCharityProject(@PathVariable String projectId) {
        var project = charityProjectService.findCharityProjectById(projectId);
        return ResponseEntity.ok(project);
    }

    // Fetch charity projects (all or by name)
    @GetMapping
    public ResponseEntity<List<CharityProject>> getCharityProjects(@RequestParam(required = false) String name) {
        List<CharityProject> result = charityProjectService.findCharityProjects(name);
        return ResponseEntity.ok(result);
    }

    // Fetch charity projects by country
    @GetMapping("/byCountry/{country}")
    public ResponseEntity<List<CharityProject>> getCharityProjectsByCountry(@PathVariable String country) {
        logger.info("API called: GET /charityProjects/search with country: {}", country);
        var result = charityProjectService.findCharityProjectsByCountry(country);
        return ResponseEntity.ok(result);
    }

    // Fetch charity projects by continent
    @GetMapping("/byContinent/{continentId}")
    public ResponseEntity<List<CharityProject>> getCharityProjectsByContinent(@PathVariable String continentId) {
        logger.info("API called: GET /charityProjects/search with category: {}", continentId);
        var result = charityProjectService.findCharityProjectsByContinent(continentId);
        System.out.print(continentId);
        return ResponseEntity.ok(result);
    }

    // Fetch charity projects by category
    @GetMapping("/byCategory")
    public ResponseEntity<List<CharityProject>> getCharityProjectByCategory(@RequestParam(required = false) String categoryName){
        System.out.println("here is "+ categoryName);
//        logger.info("API called: GET /charityProjects/search with category: {}", categoryName);
        var result = charityProjectService.searchCharityProjectByCategory(categoryName);
        return ResponseEntity.ok(result);
    }

    // Approve newly created charity project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<ProjectStatusUpdateResponse> approveCharityProject(@PathVariable("id") String projectId) {
       var project = charityProjectService.approveCharityProject(projectId);
       return ResponseEntity.ok(modelMapper.map(project, ProjectStatusUpdateResponse.class));
    }

    // Highlight charity project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/highlight/{id}")
    public ResponseEntity<CharityProject> highlightCharityProject(@PathVariable("id") String projectId) {
        try {
            // Call the service method to highlight the project
            var highlightedProject = charityProjectService.highlightCharityProject(projectId);
            return ResponseEntity.ok(highlightedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (EntityNotFound e) {
            // Return a not found response if the project does not exist
            return ResponseEntity.notFound().build();
        }
    }

    // Unhighlight charity project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/unhighlight/{id}")
    public ResponseEntity<CharityProject> unhighlightCharityProject(@PathVariable("id") String projectId) {
        try {
            // Call the service method to unhighlight the project
            var unhighlightedProject = charityProjectService.unhighlightCharityProject(projectId);
            return ResponseEntity.ok(unhighlightedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
        catch (EntityNotFound e) {
            // Return a not found response if the project does not exist
            return ResponseEntity.notFound().build();
        }
    }

    // Fetch highlighted charity projects
    @GetMapping("/get-highlighted")
    public ResponseEntity<List<CharityProject>> getHighlightedCharityProjects() {
        var highlightedProjects = charityProjectService.getHighlightedProjects();
        return ResponseEntity.ok(highlightedProjects);
    }

    //! Halt charity project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/halt/{id}")
    public ResponseEntity<CharityProject> haltCharityProject(@PathVariable("id") String projectId) {
        try {
            var haltedProject = charityProjectService.haltCharityProject(projectId);
            return ResponseEntity.ok(haltedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
        catch (EntityNotFound e) {
            // Return a not found response if the project does not exist
            return ResponseEntity.notFound().build();
        }
    }

    // Resume halted charity project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/resume/{id}")
    public ResponseEntity<CharityProject> resumeCharityProject(@PathVariable("id") String projectId) {
        try {
            var resumedProject = charityProjectService.resumeCharityProject(projectId);
            return ResponseEntity.ok(resumedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
        catch (EntityNotFound e) {
            // Return a not found response if the project does not exist
            return ResponseEntity.notFound().build();
        }
    }

    // Deactivate charity project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deactivate/{id}")
    public ResponseEntity<CharityProject> deactivateCharityProject(@PathVariable("id") String projectId) {
        try {
            var deactivatedProject = charityProjectService.deactivateCharityProject(projectId);
            return ResponseEntity.ok(deactivatedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
        catch (EntityNotFound e) {
            // Return a not found response if the project does not exist
            return ResponseEntity.notFound().build();
        }
    }

    // Restore deactivated charity project
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/restore/{id}")
    public ResponseEntity<CharityProject> restoreCharityProject(@PathVariable("id") String projectId) {
        try {
            var restoredProject = charityProjectService.restoreCharityProject(projectId);
            return ResponseEntity.ok(restoredProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
        catch (EntityNotFound e) {
            // Return a not found response if the project does not exist
            return ResponseEntity.notFound().build();
        }
    }

    // Fetch projects by status
    @GetMapping("/byStatus/{status}")
    public ResponseEntity<List<CharityProject>> getCharityProjectsByStatus(@PathVariable String status) {
        try {
            ProjectStatus projectStatus = ProjectStatus.valueOf(status.toUpperCase());
            List<CharityProject> result = charityProjectService.getProjectsByStatus(projectStatus);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ----------------- CRUD operations -----------------
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
