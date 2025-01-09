package com.teamb.statistic.services;

import com.teamb.statistic.models.Statistic;
import com.teamb.statistic.models.StatisticType;
import com.teamb.common.models.ProjectCategoryType;

import com.teamb.statistic.repositories.StatisticRepository;
import com.teamb.charity.repositories.CharityProjectRepository;
import com.teamb.donation.repositories.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private CharityProjectRepository charityProjectRepository;

    @Autowired
    private DonationRepository donationRepository;

    // Create a new statistic
    public Statistic createStatistic(Statistic statistic) {
        if (statistic.getId() == null || statistic.getId().isEmpty()) {
            statistic.setId(UUID.randomUUID().toString());
        }
        statistic.setCreatedAt(Instant.now());
        return statisticRepository.save(statistic);
    }

    // Calculate total number of projects
    public Statistic calculateTotalNumberOfProjects(List<String> userTargetIDs, String filterCountry, String filterContinent, String filterCategory, Date filterStartDate, Date filterEndDate) {
        long totalProjects = charityProjectRepository.countByUserTargetIDsAndFilters(userTargetIDs, filterCountry, filterContinent, filterCategory, filterStartDate, filterEndDate);

        Statistic statistic = new Statistic();
        statistic.setUserTargetIDs(userTargetIDs);
        statistic.setStatisticType(StatisticType.PROJECT_COUNT);
        statistic.setFilterCountry(filterCountry);
        statistic.setFilterContinent(filterContinent);
        statistic.setFilterCategory(ProjectCategoryType.valueOf(filterCategory));
        statistic.setFilterStartDate(filterStartDate);
        statistic.setFilterEndDate(filterEndDate);
        statistic.setValue(totalProjects);
        statistic.setCreatedAt(Instant.now());

        return statisticRepository.save(statistic);
    }

    // Calculate total donation value
    public Statistic calculateTotalDonationValue(List<String> userTargetIDs, String filterCountry, String filterContinent, String filterCategory, Date filterStartDate, Date filterEndDate) {
        double totalDonationValue = donationRepository.sumByUserTargetIDsAndFilters(userTargetIDs, filterCountry, filterContinent, filterCategory, filterStartDate, filterEndDate);

        Statistic statistic = new Statistic();
        statistic.setUserTargetIDs(userTargetIDs);
        statistic.setStatisticType(StatisticType.DONATION_VALUE);
        statistic.setFilterCountry(filterCountry);
        statistic.setFilterContinent(filterContinent);
        statistic.setFilterCategory(ProjectCategoryType.valueOf(filterCategory));
        statistic.setFilterStartDate(filterStartDate);
        statistic.setFilterEndDate(filterEndDate);
        statistic.setValue(totalDonationValue);
        statistic.setCreatedAt(Instant.now());

        return statisticRepository.save(statistic);
    }

    // Calculate number of projects for one user
    public Statistic calculateNumberOfProjectsForUser(String userTargetID, String filterCountry, String filterContinent, String filterCategory, Date filterStartDate, Date filterEndDate) {
        long totalProjects = charityProjectRepository.countByUserTargetIDAndFilters(userTargetID, filterCountry, filterContinent, filterCategory, filterStartDate, filterEndDate);

        Statistic statistic = new Statistic();
        statistic.setUserTargetIDs(List.of(userTargetID));
        statistic.setStatisticType(StatisticType.PROJECT_COUNT);
        statistic.setFilterCountry(filterCountry);
        statistic.setFilterContinent(filterContinent);
        statistic.setFilterCategory(ProjectCategoryType.valueOf(filterCategory));
        statistic.setFilterStartDate(filterStartDate);
        statistic.setFilterEndDate(filterEndDate);
        statistic.setValue(totalProjects);
        statistic.setCreatedAt(Instant.now());

        return statisticRepository.save(statistic);
    }

    // Calculate donation value for one user
    public Statistic calculateDonationValueForOneTarget(String userTargetID, Date filterStartDate, Date filterEndDate, boolean isDonor) {
        double totalDonationValue;

        if (isDonor) {
            totalDonationValue = donationRepository.sumDonationAmountByDonorId(userTargetID);
        } else {
            totalDonationValue = charityProjectRepository.sumDonationAmountByCharityId(userTargetID);
        }

        Statistic statistic = new Statistic();
        statistic.setUserTargetIDs(List.of(userTargetID));
        statistic.setStatisticType(StatisticType.DONATION_VALUE);
        statistic.setFilterCountry(null);
        statistic.setFilterContinent(null);
        statistic.setFilterCategory(null);
        statistic.setFilterStartDate(filterStartDate);
        statistic.setFilterEndDate(filterEndDate);
        statistic.setValue(totalDonationValue);
        statistic.setCreatedAt(Instant.now());

        return statisticRepository.save(statistic);
    }
    public Statistic calculateProjectCountForOneTarget(String userTargetID, Date filterStartDate, Date filterEndDate, boolean isDonor) {
        int totalProjectCount;

        if (isDonor) {
            totalProjectCount = donationRepository.countDistinctProjectsByDonorId(userTargetID);
        } else {
            totalProjectCount = charityProjectRepository.countProjectsByCharityId(userTargetID);
        }

        Statistic statistic = new Statistic();
        statistic.setUserTargetIDs(List.of(userTargetID));
        statistic.setStatisticType(StatisticType.DONATION_VALUE);
        statistic.setFilterCountry(null);
        statistic.setFilterContinent(null);
        statistic.setFilterCategory(null);
        statistic.setFilterStartDate(filterStartDate);
        statistic.setFilterEndDate(filterEndDate);
        statistic.setValue(totalProjectCount);
        statistic.setCreatedAt(Instant.now());

        return statisticRepository.save(statistic);
    }

    public Statistic calculateTotalDonationValue(Date filterStartDate, Date filterEndDate) {
        double totalDonationValue = donationRepository.sumAllDonationValues();

        Statistic statistic = new Statistic();
        statistic.setUserTargetIDs(null);
        statistic.setStatisticType(StatisticType.DONATION_VALUE);
        statistic.setFilterCountry(null);
        statistic.setFilterContinent(null);
        statistic.setFilterCategory(null);
        statistic.setFilterStartDate(filterStartDate);
        statistic.setFilterEndDate(filterEndDate);
        statistic.setValue(totalDonationValue);
        statistic.setCreatedAt(Instant.now());

        return statisticRepository.save(statistic);
    }
    public Statistic calculateProjectCount(List<String> userTargetIDs, String filterCountry, String filterContinent, String filterCategory, Date filterStartDate, Date filterEndDate) {
        int totalProjectCount = charityProjectRepository.countAllProjects();

        Statistic statistic = new Statistic();
        statistic.setUserTargetIDs(userTargetIDs);
        statistic.setStatisticType(StatisticType.PROJECT_COUNT);
        statistic.setFilterCountry(filterCountry);
        statistic.setFilterContinent(filterContinent);
        statistic.setFilterCategory(ProjectCategoryType.valueOf(filterCategory));
        statistic.setFilterStartDate(filterStartDate);
        statistic.setFilterEndDate(filterEndDate);
        statistic.setValue(totalProjectCount);
        statistic.setCreatedAt(Instant.now());

        return statisticRepository.save(statistic);
    }
}