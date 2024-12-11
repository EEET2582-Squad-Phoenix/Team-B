package com.teamb.charity.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.teamb.charity.models.Charity;

@Repository
public interface CharityRepository extends MongoRepository<Charity, String>{
} 
