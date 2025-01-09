package com.teamb.charity.repositories;

import com.teamb.charity.models.CharityProject;
import com.teamb.charity.models.Continent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharityProjectRepository extends MongoRepository<CharityProject, String> {
    List<CharityProject> findAllByNameContainingIgnoreCase(String name);

    List<CharityProject> findAllByContinent(Continent continent);

    @Query("select pc from charityProjects pc where pc.category = ?1")
    List<CharityProject> findByCond(String cond);

    List<CharityProject> findByIsGlobal(boolean isGlobal);

    @Query(value = "{ 'charity.id': ?0 }", fields = "{ 'raisedAmount': 1 }")
    double sumDonationAmountByCharityId(String donorId);

    // Count total number of projects by charityId
    @Query(value = "{ 'charity.id': ?0 }", count = true)
    int countProjectsByCharityId(String charityId);

     // Count total number of projects
     @Query(value = "{}", count = true)
     int countAllProjects();
}
