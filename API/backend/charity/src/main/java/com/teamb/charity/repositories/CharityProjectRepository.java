package com.teamb.charity.repositories;

import com.teamb.charity.models.CharityProject;
import com.teamb.charity.models.Continent;
import com.teamb.common.models.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharityProjectRepository extends MongoRepository<CharityProject, String> {
    List<CharityProject> findAllByNameContainingIgnoreCase(String name);

    List<CharityProject> findAllByCountry(String country);
    List<CharityProject> findAllByContinent(Continent continent);

    @Query("select pc from charityProjects pc where pc.category = ?1")
    List<CharityProject> findByCond(String cond);
    List<CharityProject> findByIsGlobal(boolean isGlobal);
    List<CharityProject> findByIsGlobalAndIsHighlighted(boolean isGlobal, boolean highlighted);

    List<CharityProject> findAllByStatus(ProjectStatus status);
}
