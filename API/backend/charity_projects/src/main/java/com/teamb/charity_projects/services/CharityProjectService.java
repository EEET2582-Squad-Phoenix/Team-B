package com.teamb.charity_projects.services;

import com.teamb.charity.utils.FieldChecking;
import com.teamb.common.exception.EntityNotFound;
import com.teamb.common.models.FundStatus;
import com.teamb.common.models.ProjectCategoryType;
import com.teamb.common.models.ProjectStatus;
import com.teamb.charity_projects.controllers.CharityProjectController;
import com.teamb.charity_projects.dtos.CountryRequest;
import com.teamb.charity_projects.dtos.UpdateProjectDTO;
import com.teamb.charity_projects.models.CharityProject;
import com.teamb.charity_projects.models.Halt;
import com.teamb.charity_projects.repositories.CharityProjectRepository;
import com.teamb.charity_projects.repositories.ContinentRepository;
import com.teamb.charity_projects.repositories.HaltRepository;
import com.teamb.charity_projects.utils.CountryToContinent;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CharityProjectService {

    @Autowired
    private HaltRepository haltRepository;

    @Autowired
    private CountryToContinent countryToContinent;

    private final CharityProjectRepository charityProjectRepository;
    private final ContinentRepository continentRepository;
    private static final Logger logger = LoggerFactory.getLogger(CharityProjectController.class);
    
    //! Validate project input
    private void validateInputProject(CharityProject charityProject) {
        if (FieldChecking.isNullOrEmpty(charityProject.getName())) {
            throw new IllegalArgumentException("Project name is missing!!");
        }
        if (FieldChecking.isNullOrEmpty(charityProject.getDescription())) {
            throw new IllegalArgumentException("Project description is missing!!");
        }
        if (Objects.isNull(charityProject.getCountry())) {
            throw new IllegalArgumentException("Project country is missing!!");
        }
        if (FieldChecking.isNegative(charityProject.getGoalAmount())) {
            throw new IllegalArgumentException("Project goal amount is missing!!");
        }
        // if (Objects.isNull(charityProject.getDuration())) {
        //     throw new IllegalArgumentException("Project duration is required");
        // }
        // if (charityProject.getStatus().equals(ProjectStatus.HALTED)) {
        //     throw new IllegalArgumentException("Project halted reason is required");
        // }
    }


    // Find charity project by id - API provided by team A
    public CharityProject findCharityProjectById(String id) {
        return charityProjectRepository.findById(id).orElseThrow(() -> new EntityNotFound("projectId", id));
    }

    // Fetch charity projects (all or by name)
    public List<CharityProject> findCharityProjects(String name) {
        if (name == null || name.isEmpty()) {
            return charityProjectRepository.findAll();
        }
        return charityProjectRepository.findAllByNameContainingIgnoreCase(name);
    }

    // Fetch charity projects by countries
    public List<CharityProject> getProjectsByCountries(List<String> countries) {
        if (countries == null || countries.isEmpty()) {
            throw new IllegalArgumentException("At least one country must be provided");
        }
        return charityProjectRepository.findByCountryIn(countries);
    }
    
    // Fetch charity projects by continent
    public List<CharityProject> findCharityProjectsByContinent(String continentId) {
        var continent = continentRepository.findById(continentId)
                .orElseThrow(() -> new EntityNotFound("continentId", continentId));
        return charityProjectRepository.findAllByContinent(continent);
    }
    
    // Fetch charity projects by categories
    public List<CharityProject> getProjectsByCategories(List<ProjectCategoryType> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("At least one category must be provided");
        }

        return charityProjectRepository.findByCategoriesIn(categories);
    }

    // Create charity project - API provided by team A
    public CharityProject saveCharityProject(CharityProject charityProject) {
        if (charityProject.getId() == null || charityProject.getId().isEmpty()) {
            charityProject.setId(UUID.randomUUID().toString());
        }
        
        validateInputProject(charityProject);
    
        // Set default values
        if (charityProject.getStatus() == null) {
            charityProject.setStatus(ProjectStatus.UNAPPROVED);
        }
        if (charityProject.getFundStatus() == null) {
            charityProject.setFundStatus(FundStatus.ONGOING);
        }
    
        // Set timestamps
        Date now = new Date();
        if (charityProject.getCreatedAt() == null) {
            charityProject.setCreatedAt(now);
        }
        charityProject.setUpdatedAt(now);
    
        return charityProjectRepository.save(charityProject);
    }

    // Update charity project - API provided by team A
    public CharityProject updateCharityProject(String id, UpdateProjectDTO updatedProject) {
        CharityProject existingProject = charityProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("projectId", id));
    

        existingProject.setName(updatedProject.getName());
        existingProject.setThumbnailUrl(updatedProject.getThumbnailUrl());
        existingProject.setVideoUrls(updatedProject.getVideoUrls());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setCountry(updatedProject.getCountry());
        existingProject.setCategories(updatedProject.getCategories());
        existingProject.setGoalAmount(updatedProject.getGoalAmount());
        existingProject.setUpdatedAt(Date.from(Instant.now()));

        CountryRequest countryRequest = new CountryRequest();
        countryRequest.setCountry(updatedProject.getCountry().toLowerCase());
        String continent = countryToContinent.getContinentByCountry(countryRequest);
        if(continent != null){
            existingProject.setContinent(countryToContinent.getContinentByCountry(countryRequest));
        }
    
        return charityProjectRepository.save(existingProject);
    }

    // Permanently delete charity project - API provided by team A
    public void deleteCharityProject(String id) {
        boolean isExisted = charityProjectRepository.existsById(id);
        if (!isExisted) {
            throw new EntityNotFound("projectId", id);
        }
        charityProjectRepository.deleteById(id);
    }

    // Approve newly created charity project
    public CharityProject approveCharityProject(String projectId) {
        CharityProject project = charityProjectRepository.findById(projectId).orElseThrow(() -> new EntityNotFound("projectId", projectId));

        // Verify if project can be approved
        switch (project.getStatus()) {
            case ACTIVE -> throw new IllegalArgumentException("This charity project is already active");
            case HALTED -> throw new IllegalStateException("Charity project is halted");
            case INACTIVATED -> throw new IllegalStateException("Charity project is deactivated");
            case COMPLETED -> throw new IllegalArgumentException("Charity project is completed");
            default -> {
                // No action needed for other statuses
            }
        }

        project.setStatus(ProjectStatus.ACTIVE);
        return charityProjectRepository.save(project);
    }

    // Highlight charity project
    public CharityProject highlightCharityProject(String projectId) {
        CharityProject project = charityProjectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFound("projectId", projectId));

        if (project.isHighlighted()) {
            throw new IllegalArgumentException("This charity project is already highlighted");
        }

        boolean isGlobal = project.isGlobal();
        if (isGlobal) {
            List<CharityProject> globalProjects = charityProjectRepository.findByIsGlobalAndHighlighted(true, true);
            if (globalProjects.size() >= 3) {
                throw new IllegalArgumentException("Cannot highlight more than 3 global projects");
            }
        } else {
            List<CharityProject> regionalProjects = charityProjectRepository.findByIsGlobalAndHighlighted(false, true);
            if (regionalProjects.size() >= 3) {
                throw new IllegalArgumentException("Cannot highlight more than 3 regional projects");
            }
        }

        project.setHighlighted(true);
        return charityProjectRepository.save(project);
    }

    // Unhighlight charity project
    public CharityProject unhighlightCharityProject(String projectId) {
        CharityProject project = charityProjectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFound("projectId", projectId));

        if (!project.isHighlighted()) {
            throw new IllegalArgumentException("This charity project is not highlighted");
        }
        project.setHighlighted(false);
        return charityProjectRepository.save(project);
    }

    // Fetch highlighted charity projects
    public List<CharityProject> getHighlightedProjects() {
        List<CharityProject> globalProjects = charityProjectRepository.findByIsGlobalAndHighlighted(true, true);
        List<CharityProject> regionalProjects = charityProjectRepository.findByIsGlobalAndHighlighted(false, true);

        List<CharityProject> highlightedProjects = new ArrayList<>();
        highlightedProjects.addAll(globalProjects);
        highlightedProjects.addAll(regionalProjects);

        return highlightedProjects;
    }

    // Halt charity project - Halt status modification API provided by team A
    public CharityProject haltCharityProject(String projectId, Halt haltReason) {
        CharityProject project = charityProjectRepository.findById(projectId).orElseThrow(() -> new EntityNotFound("projectId", projectId));
      
        // Verify if project can be halted
        switch (project.getStatus()) {
            case UNAPPROVED -> throw new IllegalArgumentException("Cannot halt unapproved charity project");
            case HALTED -> throw new IllegalArgumentException("This charity project is already halted");
            case INACTIVATED -> throw new IllegalArgumentException("This charity project is already deactivated");
            case COMPLETED -> throw new IllegalArgumentException("This charity project is already completed");
            default -> {
                // No action needed for other statuses
            }
        }
        
        haltReason.setCharityProjectId(projectId);

        haltRepository.save(haltReason);
        return charityProjectRepository.save(project);
    }

    // Resume halted charity project
    public CharityProject resumeCharityProject(String projectId) {
        CharityProject project = charityProjectRepository.findById(projectId).orElseThrow(() -> new EntityNotFound("projectId", projectId));

        // Verify if project can be resumed
        switch (project.getStatus()) {
            case UNAPPROVED -> throw new IllegalArgumentException("Cannot resume unapproved charity project");
            case ACTIVE -> throw new IllegalArgumentException("This charity project is already active");
            case INACTIVATED -> throw new IllegalArgumentException("This charity project is already deactivated");
            case COMPLETED -> throw new IllegalArgumentException("This charity project is already completed");
            default -> {
                // No action needed for other statuses
            }
        }
        
        project.setStatus(ProjectStatus.ACTIVE);
        return charityProjectRepository.save(project);
    }

    // Deactivate charity project
    public CharityProject deactivateCharityProject(String projectId) {
        CharityProject project = charityProjectRepository.findById(projectId).orElseThrow(() -> new EntityNotFound("projectId", projectId));

        // Verify if project can be deactivated
        switch (project.getStatus()) {
            case UNAPPROVED -> throw new IllegalArgumentException("Cannot deactivate unapproved charity project");
            case INACTIVATED -> throw new IllegalArgumentException("This charity project is already deactivated");
            case COMPLETED -> throw new IllegalArgumentException("This charity project is already completed");
            default -> {
                // No action needed for other statuses
            }
        }
        
        project.setStatus(ProjectStatus.INACTIVATED);
        return charityProjectRepository.save(project);
    }

    // Restore deactivated charity project
    public CharityProject restoreCharityProject(String projectId) {
        CharityProject project = charityProjectRepository.findById(projectId).orElseThrow(() -> new EntityNotFound("projectId", projectId));

        // Verify if project can be restored
        switch (project.getStatus()) {
            case UNAPPROVED -> throw new IllegalArgumentException("Cannot restore unapproved charity project");
            case ACTIVE -> throw new IllegalArgumentException("This charity project is already active");
            case COMPLETED -> throw new IllegalArgumentException("This charity project is already completed");
            default -> {
                // No action needed for other statuses
            }
        }

        project.setStatus(ProjectStatus.ACTIVE);
        return charityProjectRepository.save(project);
    }

    // Fetch projects based on multiple statuses
    public List<CharityProject> getProjectsByStatus(List<ProjectStatus> statuses) {
        return charityProjectRepository.findAllByStatusIn(statuses);
    }

    // Fetch projects owned by a charity
    public List<CharityProject> getProjectsByCharityId(String charityId) {
        return charityProjectRepository.findAllByCharityId(charityId);
    }

    // Fetch projects owned by a charity based on multiple statuses
    public List<CharityProject> getProjectsByCharityIdAndStatus(String charityId, List<ProjectStatus> statuses) {
        return charityProjectRepository.findAllByCharityIdAndStatusIn(charityId, statuses);
    }
}
