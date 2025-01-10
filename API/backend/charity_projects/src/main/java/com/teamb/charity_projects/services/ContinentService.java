package com.teamb.charity_projects.services;

import com.teamb.charity_projects.models.Continent;
import com.teamb.common.exception.EntityNotFound;
import com.teamb.charity_projects.repositories.CharityProjectRepository;
import com.teamb.charity_projects.repositories.ContinentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContinentService {

    private final ContinentRepository continentRepository;
    private final CharityProjectRepository charityProjectRepository;

    public List<Continent> findAllContinents(String country, String continent) {
        return continentRepository.findContinentsByCountryContainingIgnoreCaseAndContinentContainingIgnoreCase(country, continent);
    }

    public Continent getContinentById(String id) {
        return continentRepository.findById(id).orElseThrow(() -> new EntityNotFound("continentId", id));
    }

    public Continent createContinent(Continent continent) {
        return continentRepository.save(continent);
    }

    public Continent updateContinent(Continent continent) {
        var isExisted = continentRepository.existsById(continent.getId());
        if (!isExisted) {
            throw new EntityNotFound("continentId", continent.getId());
        }
        return continentRepository.save(continent);
    }

    public void deleteContinent(String id) {
        var continent = continentRepository.findById(id).orElseThrow(() -> new EntityNotFound("continentId", id));
        var projectsWithConId = charityProjectRepository.findAllByContinent(continent);
        var updatedProjects = projectsWithConId.stream().peek(p -> p.setContinent(null)).toList();
        charityProjectRepository.saveAll(updatedProjects);
        continentRepository.deleteById(id);
    }

}
