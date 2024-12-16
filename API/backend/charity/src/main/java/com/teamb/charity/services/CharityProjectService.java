package com.teamb.charity.services;

import com.teamb.charity.models.CharityProject;
import com.teamb.charity.repositories.CharityProjectRepository;
import com.teamb.charity.repositories.ContinentRepository;
import com.teamb.common.exception.EntityNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CharityProjectService {

    private final CharityProjectRepository charityProjectRepository;
    private final ContinentRepository continentRepository;

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
        var continent = continentRepository.findById(continentId).orElseThrow(() -> new EntityNotFound("continentId", continentId));
        return charityProjectRepository.findAllByContinent(continent);
    }

    public CharityProject saveCharityProject(CharityProject charityProject) {
        if (charityProject.getId() == null || charityProject.getId().isEmpty()) {
            charityProject.setId(UUID.randomUUID().toString());
        }
        if (charityProject.getName() == null || charityProject.getName().isEmpty()) {}
        return charityProjectRepository.save(charityProject);
    }

}
