package com.teamb.authentication.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.teamb.account.models.Account;

@Repository
public interface AccountRepository extends MongoRepository<Account, String>{
    Account findByVerificationToken(String verificationToken);
    Account findByEmail(String email);
} 
