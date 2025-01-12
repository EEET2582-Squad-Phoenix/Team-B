package com.teamb.charity_projects.repositories;

import com.teamb.charity_projects.models.ProjectCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectCategoryRepository extends MongoRepository<ProjectCategory, String> {
}
