package com.teamb.charity_projects.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.teamb.charity_projects.models.Halt;

@Repository
public interface HaltRepository extends MongoRepository<Halt, String> {
}
