package com.teamb.charity.repositories;

import com.teamb.charity.models.ProjectCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectCategoryRepository extends MongoRepository<ProjectCategory, Long> {
}
