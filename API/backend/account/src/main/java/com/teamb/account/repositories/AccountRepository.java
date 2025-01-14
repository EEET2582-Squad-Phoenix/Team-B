package com.teamb.account.repositories;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.teamb.account.models.Account;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    Account findByVerificationToken(String verificationToken);

    Account findByEmail(String email);

    @Query(value = "{ 'role': 'DONOR', 'createdAt': { $gte: ?0, $lte: ?1 } }", count=true)
    Long countDistinctDonorsByCreatedAtBetween(Date startDate, Date endDate);
}
