package com.teamb.charity.services;

import com.teamb.charity.controllers.CharityProjectController;
import com.teamb.charity.models.CharityProject;
import com.teamb.charity.repositories.CharityProjectRepository;
import com.teamb.charity.repositories.ContinentRepository;
import com.teamb.charity.utils.FieldChecking;
import com.teamb.common.exception.EntityNotFound;
import com.teamb.common.models.FundStatus;
import com.teamb.common.models.ProjectStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CharityProjectService {

    private final CharityProjectRepository charityProjectRepository;
    private final ContinentRepository continentRepository;
    private static final Logger logger = LoggerFactory.getLogger(CharityProjectController.class);

    public CharityProject findCharityProjectById(String id) {
        return charityProjectRepository.findById(id).orElseThrow(() -> new EntityNotFound("projectId", id));
    }

    public List<CharityProject> findCharityProjects(String name) {
        if (name == null || name.isEmpty()) {
            return charityProjectRepository.findAll();
        }
        return charityProjectRepository.findAllByNameContainingIgnoreCase(name);
    }

    public List<CharityProject> findCharityProjectsByContinent(String continentId) {
        var continent = continentRepository.findById(continentId)
                .orElseThrow(() -> new EntityNotFound("continentId", continentId));
        return charityProjectRepository.findAllByContinent(continent);
    }

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
        if (Objects.isNull(charityProject.getRegion())) {
            throw new IllegalArgumentException("Project region is missing!!");
        }
        if (Objects.isNull(charityProject.getDuration())) {
            throw new IllegalArgumentException("Project duration is required");
        }
        if (charityProject.getStatus().equals(ProjectStatus.HALTED) && charityProject.getHaltedReason().isEmpty()) {
            throw new IllegalArgumentException("Project halted reason is required");
        }
    }

    public CharityProject updateCharityProject(String id, CharityProject updatedProject) {
        CharityProject existingProject = charityProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("projectId", id));
    
        validateInputProject(updatedProject);

        existingProject.setUpdatedAt(Date.from(Instant.now()));
    
        return charityProjectRepository.save(existingProject);
    }

    public void deleteCharityProject(String id) {
        boolean isExisted = charityProjectRepository.existsById(id);
        if (!isExisted) {
            throw new EntityNotFound("projectId", id);
        }
        charityProjectRepository.deleteById(id);
    }

    public CharityProject approveCharityProject(String projectId) {
        CharityProject project = charityProjectRepository.findById(projectId).orElseThrow(() -> new EntityNotFound("projectId", projectId));

        //Business validation
        switch (project.getStatus()) {
            case ACTIVE -> throw new IllegalArgumentException("This charity project is already active");
            case HALTED -> throw new IllegalStateException("Charity project is halted");
            case COMPLETED -> throw new IllegalArgumentException("Charity project is completed");
        }
        //end

        project.setStatus(ProjectStatus.ACTIVE);
        return charityProjectRepository.save(project);
    }

//    public List<CharityProject> searchCharityProjectByCategory(String category) {
//        try{
//            // consider??? - return total
//            if (category == null || category.isEmpty()) {
//                return charityProjectRepository.findAll();
//            }
//            System.out.println("caterogy"+ category);
//            return charityProjectRepository.findByCond(category);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }
    // searchCharity
    public List<CharityProject> searchCharityProjectByCategory(String category) {
        try {
            System.out.println("Category: " + category);

            if (category == null || category.isEmpty()) {
                System.out.println("Fetching all charity projects");
                return charityProjectRepository.findAll();
            }

            System.out.println("Fetching charity projects for category: " + category);
            List<CharityProject> result = charityProjectRepository.findByCond(category);
//            System.out.println("Query executed successfully, found " + result.size() + " projects");
            logger.info("Find successfully");
            return result;
        } catch (Exception e) {
//            System.err.println("Error while searching charity projects: " + e.getMessage());
            logger.error("Error", e);
//            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
