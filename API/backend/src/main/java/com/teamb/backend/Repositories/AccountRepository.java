package com.teamb.backend.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.teamb.backend.Models.Account;

@Repository
public interface AccountRepository extends MongoRepository<Account, String>{
    Account findByVerificationToken(String verificationToken);
    Account findByEmail(String email);
} 
