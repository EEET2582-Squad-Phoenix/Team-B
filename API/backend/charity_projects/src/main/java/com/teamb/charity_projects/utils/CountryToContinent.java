package com.teamb.charity_projects.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.teamb.charity_projects.dtos.CountryRequest;
import com.teamb.charity_projects.dtos.CountryResponse;

@Component
public class CountryToContinent {

    @Autowired
    private RestTemplate restTemplate;

    public String getContinentByCountry(CountryRequest countryName) {
        // Construct the URL
        String url = "https://restcountries.com/v3.1/name/" + countryName.getCountry() + "?fields=continents";
        
        // Make the API request and get the response as an array of CountryResponse objects
        CountryResponse[] response = restTemplate.getForObject(url, CountryResponse[].class);
        
        // Check if the response is valid and contains continents
        if (response != null && response.length > 0 && response[0].getContinents() != null) {
            // Return the first continent in uppercase (if available)
            return response[0].getContinents()[0].toUpperCase();
        } else {
            return null;  // No continent data found
        }
    }
}