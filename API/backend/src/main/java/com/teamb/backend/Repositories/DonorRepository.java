package com.teamb.backend.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.teamb.backend.Models.Donor;

@Repository
public interface DonorRepository extends MongoRepository<Donor, String>{
} 
