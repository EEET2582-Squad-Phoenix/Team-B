package com.teamb.charity_projects.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.teamb.charity_projects.models.Continent;

import java.util.List;
// import java.util.Optional;

@Repository
public interface ContinentRepository extends MongoRepository<Continent, String> {

    List<Continent> findContinentsByCountryContainingIgnoreCaseAndContinentContainingIgnoreCase(String country, String continent);

    List<Continent>findAllByContinentContainingIgnoreCase(String continent);

    List<Continent> findContinentsByCountryContainingIgnoreCase(String country);
}
