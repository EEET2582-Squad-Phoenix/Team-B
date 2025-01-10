package com.teamb.charity_projects.repositories;

import com.teamb.charity_projects.models.CharityProject;
import com.teamb.charity_projects.models.Continent;
import com.teamb.common.models.ProjectCategoryType;
import com.teamb.common.models.ProjectStatus;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharityProjectRepository extends MongoRepository<CharityProject, String> {
    List<CharityProject> findAllByNameContainingIgnoreCase(String name);

    List<CharityProject> findByCountryIn(List<String> countries);
    List<CharityProject> findAllByContinent(Continent continent);
    List<CharityProject> findByCategoriesIn(List<ProjectCategoryType> categories);

    @Query("select pc from charityProjects pc where pc.category = ?1")
    List<CharityProject> findByCond(String cond);
    List<CharityProject> findByIsGlobal(boolean isGlobal);
    List<CharityProject> findByIsGlobalAndHighlighted(boolean isGlobal, boolean highlighted);

    List<CharityProject> findAllByStatus(ProjectStatus status);
}
