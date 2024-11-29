package com.teamb.backend.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.teamb.backend.Models.Charity;

@Repository
public interface CharityRepository extends MongoRepository<Charity, String>{
} 
