package com.teamb.statistic.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.teamb.common.models.ProjectCategoryType;
import com.teamb.statistic.models.Statistic;
import com.teamb.statistic.models.StatisticType;

@Repository
public interface StatisticRepository extends MongoRepository<Statistic, String> {
    // @Query(value = """
    //     {
    //         'statisticType': ?0,
    //         '$and': [
    //             {
    //                 '$or': [
    //                     { 'filterCountry': ?1 },
    //                     { '$and': [ { 'filterCountry': { '$exists': false } }, { ?1: null } ] }
    //                 ]
    //             },
    //             {
    //                 '$or': [
    //                     { 'filterContinent': ?2 },
    //                     { '$and': [ { 'filterContinent': { '$exists': false } }, { ?2: null } ] }
    //                 ]
    //             },
    //             {
    //                 '$or': [
    //                     { 'filterCategory': ?3 },
    //                     { '$and': [ { 'filterCategory': { '$exists': false } }, { ?3: null } ] }
    //                 ]
    //             }
    //         ]
    //     }
    //     """)
    // List<Statistic> findMatchingStatistics(
    //     StatisticType statisticType,
    //     String filterCountry,
    //     String filterContinent,
    //     String filterCategory
    // );
}
