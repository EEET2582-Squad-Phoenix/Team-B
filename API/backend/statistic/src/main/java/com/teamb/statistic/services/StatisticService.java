package com.teamb.statistic.services;

import com.teamb.charity.models.CharityProject;
import com.teamb.charity.repositories.ContinentRepository;
import com.teamb.statistic.models.Statistic;
import com.teamb.statistic.models.StatisticType;

import com.teamb.statistic.repositories.StatisticRepository;
import com.teamb.charity.repositories.CharityProjectRepository;
import com.teamb.donation.repositories.DonationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {
    // STORE VALUE TIME OF VIETNAM:0 -4-8-12-16-20-24

    private static final Integer MAXIMUM_ACCEPTABLE_OUTDATED_HRS = 4;

    private static final Function<FluentQuery.FetchableFluentQuery<Statistic>, Optional<Statistic>> GET_FIRST_ORDER_DESC_BY_CREATED_AT = (
            q) -> q.sortBy(Sort.by("createdAt").descending()).first();

    private final StatisticRepository statisticRepository;

    private final CharityProjectRepository charityProjectRepository;

    private final DonationRepository donationRepository;
    private final ContinentRepository continentRepository;

    // Calculate donation value for one user
    public Statistic calculateDonationValueForOneTarget(String userTargetID, boolean isDonor) {

        var baseValue = Statistic.builder()
                .userTargetIDs(List.of(userTargetID))
                .build();

        var statistic = statisticRepository.findBy(
                Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase()),
                GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(baseValue);

        // if statistic found and created time is not outdated
        if (statistic.getId() != null && statistic.getCreatedAt()
                .plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS, ChronoUnit.HOURS).isAfter(Instant.now())) {
            return statistic;
        }

        // Outdated... Updating new value
        double totalDonationValue = isDonor
                ? donationRepository.sumDonationAmountByDonorId(userTargetID)
                : charityProjectRepository.sumDonationAmountByCharityId(userTargetID);

        if (statistic.getId() == null) {
            statistic.setId(UUID.randomUUID().toString());
        }
        statistic.setValue(totalDonationValue);
        statistic.setCreatedAt(Instant.now());

        return statisticRepository.save(statistic);
    }

    public Statistic calculateProjectCountForOneTarget(String userTargetID, boolean isDonor) {
        var baseValue = Statistic.builder()
                .userTargetIDs(List.of(userTargetID))
                .statisticType(StatisticType.PROJECT_COUNT)
                .build();

        var statistic = statisticRepository.findBy(
                Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase()),
                GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(baseValue);

        // if statistic found and created time is not outdated
        if (statistic.getId() != null && statistic.getCreatedAt()
                .plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS, ChronoUnit.HOURS).isAfter(Instant.now())) {
            return statistic;
        }

        // Outdated... Updating new value
        int totalProjectCount = isDonor
                ? donationRepository.countDistinctProjectsByDonorId(userTargetID)
                : charityProjectRepository.countProjectsByCharityId(userTargetID);

        if (statistic.getId() == null) {
            statistic.setId(UUID.randomUUID().toString());
        }
        statistic.setValue(totalProjectCount);
        statistic.setCreatedAt(Instant.now());
        return statisticRepository.save(statistic);
    }

    /**
     * Calculate the total donation of all the projects in the system.
     * the new value will be updated if the record in the db is
     * {@value MAXIMUM_ACCEPTABLE_OUTDATED_HRS} hours ago.
     *
     * @param filterStartDate left boundary of the filter. Will be flooring to HOURS
     * @param filterEndDate   right boundary of the filter. Will be ceiling to HOURS
     * @return
     */
    public Statistic calculateTotalDonationValue(Date filterStartDate, Date filterEndDate) {
        var baseValue = Statistic.builder()
                .statisticType(StatisticType.DONATION_VALUE)
                .filterStartDate(filterStartDate)
                .filterEndDate(filterEndDate)
                .build();

        var statistic = statisticRepository
                .findBy(
                        Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase()),
                        GET_FIRST_ORDER_DESC_BY_CREATED_AT)
                .orElse(Statistic.builder()
                        .filterStartDate(filterStartDate)
                        .filterEndDate(filterEndDate)
                        .build());

        // if statistic found and created time is not outdated
        if (statistic.getId() != null && statistic.getCreatedAt()
                .plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS, ChronoUnit.HOURS).isAfter(Instant.now())) {
            return statistic;
        }

        // Outdated... Updating new value
        double totalDonationValue = donationRepository.sumAllDonationValues();

        if (statistic.getId() == null) {
            statistic.setId(UUID.randomUUID().toString());
        }
        statistic.setValue(totalDonationValue);
        statistic.setCreatedAt(Instant.now());
        return statisticRepository.save(statistic);
    }

    /**
     * Calculate the project count in the system base on filter
     * the new value will be updated if the record in the db is
     * {@value MAXIMUM_ACCEPTABLE_OUTDATED_HRS} hours ago.
     *
     * @param filter Statistic object that has filter value
     * @return
     */
    public Statistic calculateProjectCount(Statistic filter) {
        log.info("Filter values received: {}", filter);
    
        // Fetch matching statistic or create a new one
        Statistic statistic = statisticRepository
                .findBy(
                    Example.of(filter, ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnorePaths("value")
                        .withIncludeNullValues()), // Include nulls explicitly
                    GET_FIRST_ORDER_DESC_BY_CREATED_AT
                )
                .orElseGet(() -> {
                    // If no matching record exists, create a new one
                    Statistic newStatistic = Statistic.builder()
                            .id(UUID.randomUUID().toString())
                            .statisticType(filter.getStatisticType())
                            .build();
                    log.info("No existing statistic found, creating new: {}", newStatistic);
                    return newStatistic;
                });
    
        // Explicitly update fields, including nulls
        statistic.setFilterCountry(filter.getFilterCountry());
        statistic.setFilterContinent(filter.getFilterContinent()); // Allow overwriting with null
        statistic.setFilterCategory(filter.getFilterCategory());
        statistic.setFilterStartDate(filter.getFilterStartDate());
        statistic.setFilterEndDate(filter.getFilterEndDate());
    
        // If the statistic is not outdated, return it
        if (statistic.getId() != null
                && statistic.getCreatedAt() != null
                && statistic.getCreatedAt().plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS, ChronoUnit.HOURS)
                .isAfter(Instant.now())) {
            return statistic;
        }
    
        // Calculate new value if needed
        long totalProjectCount = charityProjectRepository.countBy(
                valueOrEmpty(filter.getFilterContinent()),
                valueOrEmpty(filter.getFilterCountry()),
                valueOrEmpty(filter.getFilterCategory())
        );
    
        log.info("Project count calculated: {}", totalProjectCount);
    
        statistic.setValue(totalProjectCount);
        statistic.setCreatedAt(Instant.now());
    
        return statisticRepository.save(statistic);
    }

    private String valueOrEmpty(Object o) {
        return Objects.isNull(o) ? "" : o.toString();
    }

}