package com.teamb.donation.repositories;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

import com.teamb.donation.dtos.DonationDTO;

public interface DonationRepository extends MongoRepository<DonationDTO, String> {

    // Custom query to fetch only necessary fields
    // @Query(value = "{}", fields = "{_id: 1, donor: 1, project: 1, creditCard: 1,
    // amount: 1, message: 1, status: 1, isRecurring: 1, donationDate: 1}")
    // List<DonationDTO> findAllDonations();

    // // Sum donation values by user target IDs and filters
    // @Query("{ 'userTargetID': { $in: ?0 }, 'country': ?1, 'continent': ?2,
    // 'category': ?3, 'donationDate': { $gte: ?4, $lte: ?5 } }")
    // double sumByUserTargetIDsAndFilters(List<String> userTargetIDs, String
    // country, String continent, String category, Date startDate, Date endDate);

    // // Sum donation values by a single user target ID and filters
    // @Query("{ 'userTargetID': ?0, 'country': ?1, 'continent': ?2, 'category': ?3,
    // 'donationDate': { $gte: ?4, $lte: ?5 } }")
    // double sumByUserTargetIDAndFilters(String userTargetID, String country,
    // String continent, String category, Date startDate, Date endDate);

    // Sum donation values by donor ID using aggregation
    @Aggregation(pipeline = {
        "{ '$match': { 'donor': ?0 } }",
        "{ '$group': { '_id': null, 'totalAmount': { '$sum': '$amount' } } }"
    })
    Double sumDonationAmountByDonorId(String donorId);

    // Count unique projects by donor ID using aggregation
    @Aggregation(pipeline = {
        "{ '$match': { 'donor': ?0 } }",
        "{ '$group': { '_id': '$project' } }",
        "{ '$count': 'totalProjects' }"
    })
    Double countDistinctProjectsByDonorId(String donorId);


}
