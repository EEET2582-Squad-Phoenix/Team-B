package com.teamb.donor.repositories;

import java.util.List;
import java.util.concurrent.Flow.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.teamb.donor.models.Donor;

@Repository
public interface DonorRepository extends MongoRepository<Donor, String>{
    @Query("{ $or: [ { 'firstName' : { $regex: ?0, $options: 'i' } }, { 'lastName' : { $regex: ?0, $options: 'i' } } ] }")
    List<Donor> findByFirstNameOrLastName(String name);
    List<Subscription> getSubscriptions(String id);
}
