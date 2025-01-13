package com.teamb.charity.repositories;

import com.teamb.charity.models.CharityProject;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharityProjectRepository extends MongoRepository<CharityProject, String> {
     List<CharityProject> findAllByNameContainingIgnoreCase(String name);

     List<CharityProject> findAllByContinent(String continent);

     @Query("select pc from charityProjects pc where pc.category = ?1")
     List<CharityProject> findByCond(String cond);

     List<CharityProject> findByIsGlobal(boolean isGlobal);

     @Query(value = "{ 'charityID': ?0 }", fields = "{ 'raisedAmount': 1 }")
     @Aggregation(pipeline = {
         "{ '$match': { 'charityID': ?0 } }",
         "{ '$group': { '_id': null, 'totalRaisedAmount': { '$sum': '$raisedAmount' } } }"
     })
     Double sumDonationAmountByCharityId(String charityId);

     // Count total number of projects by charityId
     @Query(value = "{ 'charityID': ?0 }", count = true)
     int countProjectsByCharityId(String charityId);

     // Count total number of projects
     @Query(value = """
               {
                    continent: {$regex: ?0},
                    country: {$regex: ?1},
                    category: {$regex: ?2}
               }
               """, count = true)
     Double countBy(String continent, String country, String category);

     @Aggregation(pipeline = {
          "{ '$match': { " +
              "'continent': { '$regex': ?0, '$options': 'i' }, " +
              "'country': { '$regex': ?1, '$options': 'i' }, " +
              "'category': { '$regex': ?2, '$options': 'i' } " +
          "} }",
          "{ '$group': { '_id': null, 'totalRaisedAmount': { '$sum': '$raisedAmount' } } }"
      })
      Double sumTotalRaisedAmountBy(String continent, String country, String category);
}
