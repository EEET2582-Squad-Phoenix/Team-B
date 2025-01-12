package com.teamb.charity.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.teamb.charity.models.Charity;
import com.teamb.common.models.CharityType;

@Repository
public interface CharityRepository extends MongoRepository<Charity, String>{
    Charity findByName(String name);
    List<Charity> findByTypeIn(List<CharityType> types);
}
