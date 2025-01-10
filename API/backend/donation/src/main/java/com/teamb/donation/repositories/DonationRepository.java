package com.teamb.donation.repositories;

// import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.data.mongodb.repository.Query;
// import java.util.List;

import com.teamb.donation.dtos.DonationDTO;

public interface DonationRepository extends MongoRepository<DonationDTO, String> {

    // Custom query to fetch only necessary fields
    // @Query(value = "{}", fields = "{_id: 1, donor: 1, project: 1, creditCard: 1, amount: 1, message: 1, status: 1, isRecurring: 1, donationDate: 1}")
    // List<DonationDTO> findAllDonations();

}
