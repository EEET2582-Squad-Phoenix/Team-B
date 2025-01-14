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

}
