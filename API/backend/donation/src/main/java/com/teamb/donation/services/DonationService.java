package com.teamb.donation.services;


import com.teamb.donation.dtos.DonationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<DonationDTO> getAllDonations() {
        // Fetch all donations from the collection with projection
        Query query = new Query();
        query.fields()
                .include("_id")
                .include("donor")
                .include("project")
                .include("creditCard")
                .include("amount")
                .include("message")
                .include("status")
                .include("isRecurring")
                .include("donationDate");

        // Query the donations collection and map to DonationDTO
        return mongoTemplate.find(query, DonationDTO.class, "donations");
    }
}
