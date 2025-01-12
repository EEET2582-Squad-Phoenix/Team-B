package com.teamb.charity_projects.controllers;

import com.teamb.charity_projects.services.CharityProjectService;
import com.teamb.charity_projects.utils.CountryToContinent;
import com.teamb.common.exception.EntityNotFound;
import com.teamb.common.models.ProjectCategoryType;
import com.teamb.common.models.ProjectStatus;
import com.teamb.charity_projects.dtos.CountryRequest;
import com.teamb.charity_projects.dtos.UpdateProjectDTO;
import com.teamb.charity_projects.models.CharityProject;
import com.teamb.charity_projects.models.Halt;
import com.teamb.charity_projects.response.ProjectStatusUpdateResponse;

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
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("admin/projects")
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
    @GetMapping("/byCountries")
    public ResponseEntity<List<CharityProject>> getCharityProjectsByCountries(@RequestParam List<String> countries) {
        try {
            List<CharityProject> projects = charityProjectService.getProjectsByCountries(countries);
            return ResponseEntity.ok(projects);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
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
    @GetMapping("/byCategories")
    public ResponseEntity<List<CharityProject>> getCharityProjectsByCategories(@RequestParam List<ProjectCategoryType> categories) {
        try {
            List<CharityProject> projects = charityProjectService.getProjectsByCategories(categories);
            return ResponseEntity.ok(projects);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
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

    // Halt charity project
    @PostMapping("/halt/{id}")
    public ResponseEntity<?> haltCharityProject(@PathVariable String id, @RequestBody Halt haltReason) {
        try {
            if (haltReason == null) {
                // Return a bad request error with a meaningful message
                return ResponseEntity.badRequest().body("Halt reason must be provided.");
            }

            CharityProject haltedProject = charityProjectService.haltCharityProject(id, haltReason);
            return ResponseEntity.ok(haltedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFound e) {
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

    // Fetch projects by multiple statuses
    @GetMapping("/byStatuses")
    public ResponseEntity<List<CharityProject>> getCharityProjectsByStatuses(@RequestParam List<ProjectStatus> statuses) {
        try {
            List<CharityProject> projects = charityProjectService.getProjectsByStatus(statuses);
            return ResponseEntity.ok(projects);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Fetch projects owned by a charity
    @GetMapping("/byCharity/{charityId}")
    public ResponseEntity<List<CharityProject>> getCharityProjectsByCharity(@PathVariable String charityId) {
        try {
            List<CharityProject> projects = charityProjectService.getProjectsByCharityId(charityId);
            return ResponseEntity.ok(projects);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Fetch projects owned by a charity based on multiple statuses
    @GetMapping("/byCharityAndStatuses/{charityId}")
    public ResponseEntity<List<CharityProject>> getCharityProjectsByCharityAndStatuses(@PathVariable String charityId, @RequestParam List<ProjectStatus> statuses) {
        try {
            List<CharityProject> projects = charityProjectService.getProjectsByCharityIdAndStatus(charityId, statuses);
            return ResponseEntity.ok(projects);
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
    public ResponseEntity<CharityProject> updateCharityProject(@RequestBody UpdateProjectDTO updateProject, @PathVariable String id)
    {
        CharityProject result = charityProjectService.updateCharityProject(id, updateProject);
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
