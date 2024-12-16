package com.teamb.charity.repositories;

import com.teamb.charity.models.Continent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContinentRepository extends MongoRepository<Continent, String> {

    List<Continent> findContinentsByCountryContainingIgnoreCaseAndContinentContainingIgnoreCase(String country, String continent);

    List<Continent>findAllByContinentContainingIgnoreCase(String continent);

    List<Continent> findContinentsByCountryContainingIgnoreCase(String country);
}
