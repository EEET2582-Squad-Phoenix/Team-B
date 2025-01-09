package com.teamb.statistic.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.teamb.statistic.models.Statistic;

@Repository
public interface StatisticRepository extends MongoRepository<Statistic, String>{

} 
