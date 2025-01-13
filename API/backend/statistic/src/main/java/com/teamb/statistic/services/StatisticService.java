package com.teamb.statistic.services;

import com.teamb.charity_projects.models.CharityProject;
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

import java.math.BigDecimal;
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

    /**
     * Calculate the total donation of all the projects in the system.
     * the new value will be updated if the record in the db is
     * {@value MAXIMUM_ACCEPTABLE_OUTDATED_HRS} hours ago.
     *
     * @param filterStartDate left boundary of the filter. Will be flooring to HOURS
     * @param filterEndDate   right boundary of the filter. Will be ceiling to HOURS
     * @return
     */
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
        Statistic newStatistic = Statistic.builder()
                .id(UUID.randomUUID().toString())
                .statisticType(StatisticType.DONATION_VALUE_SYSTEM)
                .filterCountry(filter.getFilterCountry())
                .filterContinent(filter.getFilterContinent())
                .filterCategory(filter.getFilterCategory())
                .filterStatus(filter.getFilterStatus())
                .filterStartDate(filter.getFilterStartDate())
                .filterEndDate(filter.getFilterEndDate())
                .build();
        log.info("No existing statistic found or outdated, creating new: {}", newStatistic);

        // Calculate new value
        Double totalDonationValue = charityProjectRepository.sumTotalRaisedAmountBy(
                valueOrEmpty(filter.getFilterContinent()),
                valueOrEmpty(filter.getFilterCountry()),
                valueOrEmpty(filter.getFilterStatus()),
                valueOrEmptyList(filter.getFilterCategory()));

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
        Statistic newStatistic = Statistic.builder()
                .id(UUID.randomUUID().toString())
                .statisticType(StatisticType.PROJECT_COUNT_SYSTEM)
                .filterCountry(filter.getFilterCountry())
                .filterContinent(filter.getFilterContinent())
                .filterCategory(filter.getFilterCategory())
                .filterStatus(filter.getFilterStatus())
                .filterStartDate(filter.getFilterStartDate())
                .filterEndDate(filter.getFilterEndDate())
                .build();
        log.info("No existing statistic found or outdated, creating new: {}", newStatistic);

        // Calculate new value
        Double totalProjectCount = charityProjectRepository.countBy(
                valueOrEmpty(filter.getFilterContinent()),
                valueOrEmpty(filter.getFilterCountry()),
                valueOrEmptyList(filter.getFilterCategory()));
                valueOrEmpty(filter.getFilterStatus());
        if (totalProjectCount == null) {
            totalProjectCount = 0.0;
        }

        newStatistic.setValue(totalProjectCount);
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

    private String valueOrEmpty(Object o) {
        return Objects.isNull(o) ? "" : o.toString();
    }

    private List<String> valueOrEmptyList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return List.of(".*"); // Match any string
        }
        return list.stream()
                .map(Pattern::quote) // Escape special characters
                .collect(Collectors.toList());
    }

}
// package com.teamb.statistic.services;

// import com.teamb.charity.models.CharityProject;
// import com.teamb.charity.repositories.ContinentRepository;
// import com.teamb.common.models.ProjectCategoryType;
// import com.teamb.statistic.models.Statistic;
// import com.teamb.statistic.models.StatisticType;

// import com.teamb.statistic.repositories.StatisticRepository;
// import com.teamb.charity.repositories.CharityProjectRepository;
// import com.teamb.donation.repositories.DonationRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.data.domain.Example;
// import org.springframework.data.domain.ExampleMatcher;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.repository.query.FluentQuery;
// import org.springframework.stereotype.Service;

// import org.springframework.data.mongodb.core.query.Query;
// import java.time.Instant;
// import java.time.temporal.ChronoUnit;
// import java.util.*;
// import java.util.function.Function;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class StatisticService {
// // STORE VALUE TIME OF VIETNAM:0 -4-8-12-16-20-24

// private static final Integer MAXIMUM_ACCEPTABLE_OUTDATED_HRS = 4;

// private static final Function<FluentQuery.FetchableFluentQuery<Statistic>,
// Optional<Statistic>> GET_FIRST_ORDER_DESC_BY_CREATED_AT = (
// q) -> q.sortBy(Sort.by("createdAt").descending()).first();

// private final StatisticRepository statisticRepository;

// private final CharityProjectRepository charityProjectRepository;

// private final ContinentRepository continentRepository;
// private final DonationRepository donationRepository;
// private final MongoTemplate mongoTemplate;

// // Calculate donation value for one user
// public Statistic calculateDonationValueForOneTarget(String userTargetID,
// boolean isDonor) {

// var baseValue = Statistic.builder()
// .userTargetIDs(List.of(userTargetID))
// .build();

// var statistic = statisticRepository.findBy(
// Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase()),
// GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(baseValue);

// // if statistic found and created time is not outdated
// if (statistic.getId() != null && statistic.getCreatedAt()
// .plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS,
// ChronoUnit.HOURS).isAfter(Instant.now())) {
// return statistic;
// }

// // Outdated... Updating new value
// double totalDonationValue = isDonor
// ? donationRepository.sumDonationAmountByDonorId(userTargetID)
// : charityProjectRepository.sumDonationAmountByCharityId(userTargetID);

// if (statistic.getId() == null) {
// statistic.setId(UUID.randomUUID().toString());
// }
// statistic.setValue(totalDonationValue);
// statistic.setCreatedAt(Instant.now());

// return statisticRepository.save(statistic);
// }

// public Statistic calculateProjectCountForOneTarget(String userTargetID,
// boolean isDonor) {
// var baseValue = Statistic.builder()
// .userTargetIDs(List.of(userTargetID))
// .statisticType(StatisticType.PROJECT_COUNT)
// .build();

// var statistic = statisticRepository.findBy(
// Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase()),
// GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(baseValue);

// // if statistic found and created time is not outdated
// if (statistic.getId() != null && statistic.getCreatedAt()
// .plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS,
// ChronoUnit.HOURS).isAfter(Instant.now())) {
// return statistic;
// }

// // Outdated... Updating new value
// int totalProjectCount = isDonor
// ? donationRepository.countDistinctProjectsByDonorId(userTargetID)
// : charityProjectRepository.countProjectsByCharityId(userTargetID);

// if (statistic.getId() == null) {
// statistic.setId(UUID.randomUUID().toString());
// }
// statistic.setValue(totalProjectCount);
// statistic.setCreatedAt(Instant.now());
// return statisticRepository.save(statistic);
// }

// /**
// * Calculate the total donation of all the projects in the system.
// * the new value will be updated if the record in the db is
// * {@value MAXIMUM_ACCEPTABLE_OUTDATED_HRS} hours ago.
// *
// * @param filterStartDate left boundary of the filter. Will be flooring to
// HOURS
// * @param filterEndDate right boundary of the filter. Will be ceiling to HOURS
// * @return
// */
// public Statistic calculateTotalDonationValue(Statistic filter) {
// log.info("Filter values received: {}", filter);

// // Fetch matching statistic
// Optional<Statistic> optionalStatistic = statisticRepository
// .findBy(
// Example.of(filter, ExampleMatcher.matching()
// .withIgnoreCase()
// .withIgnorePaths("value")
// .withIncludeNullValues()), // Include nulls explicitly
// GET_FIRST_ORDER_DESC_BY_CREATED_AT);

// if (optionalStatistic.isPresent()) {
// Statistic existingStatistic = optionalStatistic.get();
// // Check if the statistic is outdated
// if (existingStatistic.getCreatedAt() != null
// && existingStatistic.getCreatedAt().plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS,
// ChronoUnit.HOURS)
// .isAfter(Instant.now())) {
// return existingStatistic;
// }
// }

// // Create a new Statistic object if no matching record exists or if it is
// // outdated
// Statistic newStatistic = Statistic.builder()
// .id(UUID.randomUUID().toString())
// .statisticType(filter.getStatisticType())
// .filterCountry(filter.getFilterCountry())
// .filterContinent(filter.getFilterContinent())
// .filterCategory(filter.getFilterCategory())
// .filterStartDate(filter.getFilterStartDate())
// .filterEndDate(filter.getFilterEndDate())
// .build();
// log.info("No existing statistic found or outdated, creating new: {}",
// newStatistic);

// // Calculate new value
// Double totalDonationValue = charityProjectRepository.sumTotalRaisedAmountBy(
// valueOrEmpty(filter.getFilterContinent()),
// valueOrEmpty(filter.getFilterCountry()),
// valueOrEmpty(filter.getFilterCategory()));

// if (totalDonationValue == null) {
// totalDonationValue = 0.0;
// }

// newStatistic.setValue(totalDonationValue);
// newStatistic.setCreatedAt(Instant.now());

// return statisticRepository.save(newStatistic);
// }

// /**
// * Calculate the project count in the system base on filter
// * the new value will be updated if the record in the db is
// * {@value MAXIMUM_ACCEPTABLE_OUTDATED_HRS} hours ago.
// *
// * @param filter Statistic object that has filter value
// * @return
// */
// public Statistic calculateProjectCount(Statistic filter) {
// log.info("Filter values received: {}", filter);

// // Normalize filter values
// filter.setFilterContinent(valueOrNull(filter.getFilterContinent()));
// filter.setFilterCountry(valueOrNull(filter.getFilterCountry()));
// filter.setFilterCategory(valueOrNull(filter.getFilterCategory()));

// log.info("Normalized filter values: filterContinent={}, filterCountry={},
// filterCategory={}",
// filter.getFilterContinent(), filter.getFilterCountry(),
// filter.getFilterCategory());

// // Dynamically build query
// Query query = new Query();
// query.addCriteria(Criteria.where("statisticType").is(filter.getStatisticType()));

// if (filter.getFilterCountry() != null) {
// query.addCriteria(Criteria.where("filterCountry").is(filter.getFilterCountry()));
// } else {
// query.addCriteria(new Criteria().orOperator(
// Criteria.where("filterCountry").is(null),
// Criteria.where("filterCountry").exists(false)
// ));
// }

// if (filter.getFilterContinent() != null) {
// query.addCriteria(Criteria.where("filterContinent").is(filter.getFilterContinent()));
// } else {
// query.addCriteria(new Criteria().orOperator(
// Criteria.where("filterContinent").is(null),
// Criteria.where("filterContinent").exists(false)
// ));
// }

// if (filter.getFilterCategory() != null) {
// query.addCriteria(Criteria.where("filterCategory").is(filter.getFilterCategory()));
// } else {
// query.addCriteria(new Criteria().orOperator(
// Criteria.where("filterCategory").is(null),
// Criteria.where("filterCategory").exists(false)
// ));
// }

// log.info("Query built: {}", query);

// // Execute query
// List<Statistic> matchingStatistics = mongoTemplate.find(query,
// Statistic.class);

// if (!matchingStatistics.isEmpty()) {
// Statistic mostRecentStatistic = matchingStatistics.stream()
// .max(Comparator.comparing(Statistic::getCreatedAt))
// .orElse(null);

// if (mostRecentStatistic != null) {
// log.info("Matching statistic found: {}", mostRecentStatistic);

// // Check if the statistic is outdated
// if (mostRecentStatistic.getCreatedAt() != null
// && mostRecentStatistic.getCreatedAt()
// .plus(MAXIMUM_ACCEPTABLE_OUTDATED_HRS, ChronoUnit.HOURS)
// .isAfter(Instant.now())) {
// return mostRecentStatistic;
// }
// }
// }

// // Create a new Statistic object if no matching record exists or if it is
// outdated
// Statistic newStatistic = Statistic.builder()
// .id(UUID.randomUUID().toString())
// .statisticType(filter.getStatisticType())
// .filterCountry(filter.getFilterCountry())
// .filterContinent(filter.getFilterContinent())
// .filterCategory(filter.getFilterCategory())
// .filterStartDate(filter.getFilterStartDate())
// .filterEndDate(filter.getFilterEndDate())
// .build();

// log.info("No existing statistic found or outdated, creating new: {}",
// newStatistic);

// // Calculate new project count
// long totalProjectCount = charityProjectRepository.countBy(
// valueOrEmpty(filter.getFilterContinent()),
// valueOrEmpty(filter.getFilterCountry()),
// valueOrEmpty(filter.getFilterCategory())
// );

// newStatistic.setValue(totalProjectCount);
// newStatistic.setCreatedAt(Instant.now());

// return statisticRepository.save(newStatistic);
// }

// /**
// * Converts an empty string or "null" string to null, otherwise returns the
// * value.
// *
// * @param value The string value to normalize.
// * @return Null if the value is an empty string or "null", otherwise the
// * original value.
// */
// private String valueOrNull(String value) {
// return (value == null || value.trim().isEmpty() ||
// "null".equalsIgnoreCase(value)) ? null : value;
// }

// /**
// * Returns an empty string for null values or the original string.
// *
// * @param value The object to convert.
// * @return An empty string if the value is null, otherwise the string
// * representation.
// */
// private String valueOrEmpty(Object value) {
// return value == null ? "" : value.toString();

// }
// }