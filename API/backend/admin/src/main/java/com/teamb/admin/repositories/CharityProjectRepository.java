package com.teamb.admin.repositories;

import com.teamb.admin.models.CharityProject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharityProjectRepository extends MongoRepository<CharityProject, String> {
}
