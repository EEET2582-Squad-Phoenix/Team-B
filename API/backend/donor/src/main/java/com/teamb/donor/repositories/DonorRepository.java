package com.teamb.donor.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.teamb.donor.models.Donor;

@Repository
public interface DonorRepository extends MongoRepository<Donor, String>{
    
} 
