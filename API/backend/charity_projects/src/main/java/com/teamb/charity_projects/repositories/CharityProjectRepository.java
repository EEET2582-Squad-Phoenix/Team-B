package com.teamb.charity_projects.repositories;

import com.teamb.charity_projects.models.CharityProject;
import com.teamb.charity_projects.models.Continent;
import com.teamb.common.models.ProjectCategoryType;
import com.teamb.common.models.ProjectStatus;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CharityProjectRepository extends MongoRepository<CharityProject, String> {

    List<CharityProject> findAllByNameContainingIgnoreCase(String name);

    List<CharityProject> findAllByContinent(String continent);

    @Query("select pc from charityProjects pc where pc.category = ?1")
    List<CharityProject> findByCond(String cond);

    List<CharityProject> findByIsGlobal(boolean isGlobal);



    @Aggregation(pipeline = {
        "{ '$match': { " +
                "'categories': { '$in': ?0 }, " +
                "'continent': { '$regex': ?1, '$options': 'i' }, " +
                "'country': { '$regex': ?2, '$options': 'i' }, " +
                "'status': { '$in': ?3 }, " +
                "'startDate': { '$gte': ?4 }, " +
                "'endDate': { '$lte': ?5 } " +
                "} }",
        "{ '$group': { '_id': null, 'totalRaisedAmount': { '$sum': '$raisedAmount' } } }"
})
Double sumTotalRaisedAmountBy(List<ProjectCategoryType> categories, String continent, String country, List<ProjectStatus> status, Date startDate, Date endDate);

    List<CharityProject> findByCountryIn(List<String> countries);

    List<CharityProject> findAllByContinent(Continent continent);

    List<CharityProject> findByCategoriesIn(List<ProjectCategoryType> categories);

    List<CharityProject> findByIsGlobalAndHighlighted(boolean isGlobal, boolean highlighted);

    List<CharityProject> findAllByStatus(ProjectStatus status);

    List<CharityProject> findAllByStatusIn(List<ProjectStatus> statuses);

    List<CharityProject> findAllByCharityId(String id);

    List<CharityProject> findAllByCharityIdAndStatusIn(String charityId, List<ProjectStatus> statuses);

    @Query(value = "{ 'charity.id': ?0 }", fields = "{ 'raisedAmount': 1 }")
    double sumDonationAmountByCharityId(String donorId);

    // Count total number of projects by charityId
    @Query(value = "{ 'charity.id': ?0 }", count = true)
    int countProjectsByCharityId(String charityId);

    Long countAllByCategoriesContainingAndContinentMatchesRegexAndCountryMatchesRegexAndStatusInAndStartDateGreaterThanEqualAndEndDateLessThanEqual
            (List<ProjectCategoryType> categories, String continent, String country, List<ProjectStatus> status, Date startDate, Date endDate);

}