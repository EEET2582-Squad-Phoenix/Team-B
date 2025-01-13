package com.teamb.charity_projects.repositories;

import com.teamb.charity_projects.models.CharityProject;
import com.teamb.charity_projects.models.Continent;
import com.teamb.common.models.ProjectCategoryType;
import com.teamb.common.models.ProjectStatus;

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
                 'category': { '$in': ?2 }
            }
            """, count = true)
    Double countBy(String continent, String country, List<String> category);

    @Aggregation(pipeline = {
            "{ '$match': { " +
                    "'continent': { '$regex': ?0, '$options': 'i' }, " +
                    "'country': { '$regex': ?1, '$options': 'i' }, " +
                    "'category': { '$in': ?2 } " +
                    "} }",
            "{ '$group': { '_id': null, 'totalRaisedAmount': { '$sum': '$raisedAmount' } } }"
    })
    Double sumTotalRaisedAmountBy(String continent, String country, List<String> category);

    List<CharityProject> findByCountryIn(List<String> countries);

    List<CharityProject> findAllByContinent(Continent continent);

    List<CharityProject> findByCategoriesIn(List<ProjectCategoryType> categories);

    List<CharityProject> findByIsGlobalAndHighlighted(boolean isGlobal, boolean highlighted);

    List<CharityProject> findAllByStatus(ProjectStatus status);

    List<CharityProject> findAllByStatusIn(List<ProjectStatus> statuses);

    List<CharityProject> findAllByCharityId(String id);

    List<CharityProject> findAllByCharityIdAndStatusIn(String charityId, List<ProjectStatus> statuses);

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