package com.teamb.statistic.services;

import com.teamb.common.models.ProjectCategoryType;
import com.teamb.common.models.ProjectStatus;
import com.teamb.statistic.models.Statistic;
import com.teamb.statistic.models.StatisticType;

import com.teamb.statistic.repositories.StatisticRepository;
import com.teamb.charity_projects.repositories.CharityProjectRepository;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    // private final ContinentRepository continentRepository;

    // Calculate donation value for one user
    public Statistic calculateDonationValueForOneTarget(String userTargetID, boolean isDonor) {
        log.info("Calculating donation value for userTargetID: {}, isDonor: {}", userTargetID, isDonor);

        var baseValue = Statistic.builder()
                .userTargetIDs(List.of(userTargetID))
                .statisticType(StatisticType.DONATION_VALUE_USER)
                .build();

        log.info("Base value for statistic: {}", baseValue);

        var statistic = statisticRepository.findBy(
                Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase()),
                GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(baseValue);

        log.info("Statistic found: {}", statistic);

        // if statistic found and created time is not outdated
        if (statistic.getId() != null && statistic.getCreatedAt()
                .plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS, ChronoUnit.HOURS).isAfter(Instant.now())) {
            log.info("Statistic is not outdated. Returning existing statistic.");
            return statistic;
        }

        log.info("Statistic is outdated or not found. Updating new value.");

        Double totalDonationValue = isDonor
                ? donationRepository.sumDonationAmountByDonorId(userTargetID)
                : charityProjectRepository.sumDonationAmountByCharityId(userTargetID);

        log.info("Total donation value calculated: {}", totalDonationValue);

        if (totalDonationValue == null) {
            totalDonationValue = 0.0;
            log.info("Total donation value was null, set to 0.0");
        }

        if (statistic.getId() == null) {
            statistic.setId(UUID.randomUUID().toString());
            log.info("Generated new statistic ID: {}", statistic.getId());
        }

        statistic.setValue(totalDonationValue);
        statistic.setCreatedAt(Instant.now());

        log.info("Saving updated statistic: {}", statistic);

        return statisticRepository.save(statistic);
    }

    // Calculate project count for one user
    public Statistic calculateProjectCountForOneTarget(String userTargetID, boolean isDonor) {
        log.info("Calculating project count for userTargetID: {}, isDonor: {}", userTargetID, isDonor);

        var baseValue = Statistic.builder()
                .userTargetIDs(List.of(userTargetID))
                .statisticType(StatisticType.PROJECT_COUNT_USER)
                .build();

        log.info("Base value for statistic: {}", baseValue);

        var statistic = statisticRepository.findBy(
                Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase()),
                GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(baseValue);

        log.info("Statistic found: {}", statistic);

        // if statistic found and created time is not outdated
        if (statistic.getId() != null && statistic.getCreatedAt()
                .plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS, ChronoUnit.HOURS).isAfter(Instant.now())) {
            log.info("Statistic is not outdated. Returning existing statistic.");
            return statistic;
        }

        log.info("Statistic is outdated or not found. Updating new value.");

        Double totalProjectCount = isDonor
                ? donationRepository.countDistinctProjectsByDonorId(userTargetID)
                : charityProjectRepository.countProjectsByCharityId(userTargetID);

        log.info("Total project count calculated: {}", totalProjectCount);

        if (statistic.getId() == null) {
            statistic.setId(UUID.randomUUID().toString());
            log.info("Generated new statistic ID: {}", statistic.getId());
        }

        statistic.setValue(totalProjectCount);
        statistic.setCreatedAt(Instant.now());

        log.info("Saving updated statistic: {}", statistic);

        return statisticRepository.save(statistic);
    }


    // calculate total donation value for the system
    public Statistic calculateTotalDonationValue(Statistic filter) {
        log.info("Filter values received: {}", filter);
        List<Statistic> allStatistics = statisticRepository.findAll();
        for (Statistic existingStatistic : allStatistics) {
            if (isMatchingStatistic(existingStatistic, filter)) {
                // Check if the statistic is outdated
                if (existingStatistic.getCreatedAt() != null
                        && existingStatistic.getCreatedAt().plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS, ChronoUnit.HOURS)
                                .isAfter(Instant.now())) {
                    return existingStatistic;
                }
            }
        }

        // Create a new Statistic object if no matching record exists or if it is
        // outdated
        log.info("No existing statistic found or outdated, creating new...");
        var newStatistic = filter;
        newStatistic.setId(UUID.randomUUID().toString());
        newStatistic.setStatisticType(StatisticType.DONATION_VALUE_SYSTEM);

        // Calculate new value
        Double totalDonationValue = charityProjectRepository.sumTotalRaisedAmountBy(
                filter.getFilterCategory().isEmpty() ? Arrays.asList(ProjectCategoryType.values())
                        : filter.getFilterCategory(),
                filter.getFilterContinent(),
                filter.getFilterCountry(),
                filter.getFilterStatus().isEmpty() ? Arrays.asList(ProjectStatus.values())
                        : filter.getFilterStatus(),
                filter.getFilterStartDate() != null ? filter.getFilterStartDate() : Date.from(Instant.EPOCH),
                filter.getFilterEndDate() != null ? filter.getFilterEndDate()
                        : Date.from(Instant.ofEpochMilli(Long.MAX_VALUE)));

        log.info("Total donation value before null check: {}", totalDonationValue);

        if (totalDonationValue == null) {
            totalDonationValue = 0.0;
            log.info("Total donation value was null, set to 0.0");
        }

        log.info("Total donation value after null check: {}", totalDonationValue);

        newStatistic.setValue(totalDonationValue);
        newStatistic.setCreatedAt(Instant.now());
        return statisticRepository.save(newStatistic);
    }

    // Calculate project count for the system
    public Statistic calculateProjectCount(Statistic filter) {
        log.info("Filter values received: {}", filter);
        // Fetch all statistics
        List<Statistic> allStatistics = statisticRepository.findAll();
        // Loop through and compare with filter
        for (Statistic existingStatistic : allStatistics) {
            if (isMatchingStatistic(existingStatistic, filter)) {
                // Check if the statistic is outdated
                if (existingStatistic.getCreatedAt() != null
                        && existingStatistic.getCreatedAt().plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS, ChronoUnit.HOURS)
                                .isAfter(Instant.now())) {
                    return existingStatistic;
                }
            }
        }
        // Create a new Statistic object if no matching record exists or if it is
        // outdated
        log.info("No existing statistic found or outdated, creating new...");
        var newStatistic = filter;
        newStatistic.setId(UUID.randomUUID().toString());
        newStatistic.setStatisticType(StatisticType.PROJECT_COUNT_SYSTEM);

        // Calculate new value
        Long totalProjectCount = charityProjectRepository
                .countAllByCategoriesContainingAndContinentMatchesRegexAndCountryMatchesRegexAndStatusInAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                        newStatistic.getFilterCategory(),
                        filter.getFilterContinent(),
                        filter.getFilterCountry(),
                        filter.getFilterStatus(),
                        filter.getFilterStartDate(),
                        filter.getFilterEndDate());
        if (totalProjectCount == null) {
            totalProjectCount = 0L;
        }

        newStatistic.setValue(totalProjectCount.doubleValue());
        newStatistic.setCreatedAt(Instant.now());
        return statisticRepository.save(newStatistic);
    }

    private boolean isMatchingStatistic(Statistic existingStatistic, Statistic filter) {
        return Objects.equals(existingStatistic.getStatisticType(), filter.getStatisticType())
                && Objects.equals(existingStatistic.getFilterCountry(), filter.getFilterCountry())
                && Objects.equals(existingStatistic.getFilterContinent(), filter.getFilterContinent())
                && Objects.equals(existingStatistic.getFilterCategory(), filter.getFilterCategory())
                && Objects.equals(existingStatistic.getFilterStatus(), filter.getFilterStatus())
                && Objects.equals(existingStatistic.getFilterStartDate(), filter.getFilterStartDate())
                && Objects.equals(existingStatistic.getFilterEndDate(), filter.getFilterEndDate());
    }

}
