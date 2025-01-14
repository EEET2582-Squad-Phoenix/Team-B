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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {

    private static final Function<FluentQuery.FetchableFluentQuery<Statistic>, Optional<Statistic>> GET_FIRST_ORDER_DESC_BY_CREATED_AT = (
            q) -> q.sortBy(Sort.by("createdAt").descending()).first();

    private final StatisticRepository statisticRepository;

    private final CharityProjectRepository charityProjectRepository;

    private final DonationRepository donationRepository;
    
    // Check if it's time to reset the statistics (every 4 hours)
    public Instant shouldResetStatistics() {
        // Get the current time in UTC
        Instant now = Instant.now();
        
        // Extract the hour, minute, and second in UTC
        int resetTimeNowHour = now.atZone(ZoneOffset.UTC).getHour();
        int resetTimeNowMinute = now.atZone(ZoneOffset.UTC).getMinute();
        int resetTimeNowSecond = now.atZone(ZoneOffset.UTC).getSecond();
        
        // Check if the hour is a multiple of 4 and the minute and second are exactly 0
        if (resetTimeNowMinute == 0 && resetTimeNowSecond == 0 && resetTimeNowHour % 4 == 0) {
            return ZonedDateTime.ofInstant(now, ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS).withHour(resetTimeNowHour).toInstant(); // Return the exact reset time
        }
        
        // If it's not time to reset, return null
        return null;
    }

    // Calculate donation value for one target
    public Statistic calculateDonationValueForOneTarget(String userTargetID, boolean isDonor) {
        log.info("Calculating donation value for userTargetID: {}, isDonor: {}", userTargetID, isDonor);

        // Create a base Statistic object for searching
        var baseValue = Statistic.builder()
                .userTargetIDs(List.of(userTargetID))
                .statisticType(StatisticType.DONATION_VALUE_USER)
                .build();

        log.info("Base value for statistic: {}", baseValue);

        // Always fetch the latest statistic from the database
        var statistic = statisticRepository.findBy(
            Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase().withIgnorePaths("value")),
            GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(null);

        log.info("Latest statistic found: {}", statistic);

        Instant resetTime = shouldResetStatistics();

        // If reset time is available and statistic is either null or behind the reset time, create a new statistic
        if (resetTime != null && (statistic == null || statistic.getCreatedAt().isBefore(resetTime))) {
            log.info("Statistic is missing or outdated. Creating a new statistic.");

            statistic = Statistic.builder()
                    .id(UUID.randomUUID().toString())
                    .userTargetIDs(List.of(userTargetID))
                    .statisticType(StatisticType.DONATION_VALUE_USER)
                    .createdAt(resetTime) // Set the reset time as the creation time
                    .build();
        }

        // Calculate the total donation value
        Double totalDonationValue = isDonor
                ? donationRepository.sumDonationAmountByDonorId(userTargetID)
                : charityProjectRepository.sumDonationAmountByCharityId(userTargetID);

        if (totalDonationValue == null) {
            totalDonationValue = 0.0;
            log.info("Total donation value was null, set to 0.0");
        }

        // Update the statistic with the latest values
        statistic.setValue(totalDonationValue);
        statistic.setCreatedAt(Instant.now());

        log.info("Saving updated statistic: {}", statistic);

        // Save the updated statistic to the database and return it
        return statisticRepository.save(statistic);
    }
    
    // Calculate project count for one target
    public Statistic calculateProjectCountForOneTarget(String userTargetID, boolean isDonor) {
        log.info("Calculating project count for userTargetID: {}, isDonor: {}", userTargetID, isDonor);

        // Create a base Statistic object for searching
        var baseValue = Statistic.builder()
                .userTargetIDs(List.of(userTargetID))
                .statisticType(StatisticType.PROJECT_COUNT_USER)
                .build();

        log.info("Base value for statistic: {}", baseValue);

        // Always fetch the latest statistic from the database
        var statistic = statisticRepository.findBy(
                Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase()),
                GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(null);

        log.info("Latest statistic found: {}", statistic);

        Instant resetTime = shouldResetStatistics();

        // If reset time is available and statistic is either null or behind the reset time, create a new statistic
        if (resetTime != null && (statistic == null || statistic.getCreatedAt().isBefore(resetTime))) {
            log.info("Statistic is missing or outdated. Creating a new statistic.");

            statistic = Statistic.builder()
                    .id(UUID.randomUUID().toString())
                    .userTargetIDs(List.of(userTargetID))
                    .statisticType(StatisticType.PROJECT_COUNT_USER)
                    .createdAt(resetTime) // Set the reset time as the creation time
                    .build();
        }

        // Calculate the total project count
        Double totalProjectCount = isDonor
                ? donationRepository.countDistinctProjectsByDonorId(userTargetID)
                : charityProjectRepository.countProjectsByCharityId(userTargetID);

        if (totalProjectCount == null) {
            totalProjectCount = 0.0;
            log.info("Total project count was null, set to 0.0");
        }

        // Update the statistic with the latest values
        statistic.setValue(totalProjectCount);
        statistic.setCreatedAt(Instant.now());

        log.info("Saving updated statistic: {}", statistic);

        // Save the updated statistic to the database and return it
        return statisticRepository.save(statistic);
    }

    // Calculate total donation value using the filter
    public Statistic calculateTotalDonationValue(Statistic filter) {
        log.info("Filter values received: {}", filter);

        // Always fetch all relevant statistics from the database
        List<Statistic> allStatistics = statisticRepository.findAll();

        Instant resetTime = shouldResetStatistics(); // Get the reset time

        // Get the most recently created statistic matching the filter
        Statistic mostRecentStatistic = allStatistics.stream()
                .filter(existingStatistic -> isMatchingStatistic(existingStatistic, filter))
                .max(Comparator.comparing(Statistic::getCreatedAt))
                .orElse(null); // If no matching statistic, return null

        // If there's no statistic or the most recent one is outdated, create a new one
        if (mostRecentStatistic == null || (resetTime != null && mostRecentStatistic.getCreatedAt().isBefore(resetTime))) {
            log.info("No valid matching statistic found or reset is due. Creating new statistic...");

            // Create the new Statistic object with the necessary filter values
            var newStatistic = Statistic.builder()
                    .id(UUID.randomUUID().toString())
                    .statisticType(StatisticType.DONATION_VALUE_SYSTEM)
                    .filterCategory(filter.getFilterCategory())
                    .filterContinent(filter.getFilterContinent())
                    .filterCountry(filter.getFilterCountry())
                    .filterStatus(filter.getFilterStatus())
                    .filterStartDate(filter.getFilterStartDate())
                    .filterEndDate(filter.getFilterEndDate())
                    .build();

            // Calculate the total donation value
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

            // Update and save the new statistic
            newStatistic.setValue(totalDonationValue);
            newStatistic.setCreatedAt(Instant.now());

            log.info("Saving new statistic: {}", newStatistic);

            // Save the new statistic and return it
            return statisticRepository.save(newStatistic);
        }

        // Return the most recent statistic if no reset is needed
        log.info("Found valid matching statistic: {}", mostRecentStatistic);
        return mostRecentStatistic;
    }

    // Calculate total project count using the filter
    public Statistic calculateProjectCount(Statistic filter) {
        log.info("Filter values received: {}", filter);

        // Always fetch all relevant statistics from the database
        List<Statistic> allStatistics = statisticRepository.findAll();

        Instant resetTime = shouldResetStatistics(); // Get the reset time

        // Get the most recently created statistic matching the filter
        Statistic mostRecentStatistic = allStatistics.stream()
                .filter(existingStatistic -> isMatchingStatistic(existingStatistic, filter))
                .max(Comparator.comparing(Statistic::getCreatedAt))
                .orElse(null); // If no matching statistic, return null

        // If there's no statistic or the most recent one is outdated, create a new one
        if (mostRecentStatistic == null || (resetTime != null && mostRecentStatistic.getCreatedAt().isBefore(resetTime))) {
            log.info("No valid matching statistic found or reset is due. Creating new statistic...");

            // Create the new Statistic object with the necessary filter values
            var newStatistic = Statistic.builder()
                    .id(UUID.randomUUID().toString())
                    .statisticType(StatisticType.PROJECT_COUNT_SYSTEM)
                    .filterCategory(filter.getFilterCategory())
                    .filterContinent(filter.getFilterContinent())
                    .filterCountry(filter.getFilterCountry())
                    .filterStatus(filter.getFilterStatus())
                    .filterStartDate(filter.getFilterStartDate())
                    .filterEndDate(filter.getFilterEndDate())
                    .build();

            // Calculate the total project count
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
                log.info("Total project count was null, set to 0.");
            }

            log.info("Total project count calculated: {}", totalProjectCount);

            // Update and save the new statistic
            newStatistic.setValue(totalProjectCount.doubleValue());
            newStatistic.setCreatedAt(Instant.now());

            log.info("Saving new statistic: {}", newStatistic);

            // Save the new statistic and return it
            return statisticRepository.save(newStatistic);
        }

        // Return the most recent statistic if no reset is needed
        log.info("Found valid matching statistic: {}", mostRecentStatistic);
        return mostRecentStatistic;
    }
    
    // Calculate nnumber of new DONOR registrations (last 24 hours compared to the calculated reset time)
    public Statistic calculateNewDonorRegistrations() {
        log.info("Calculating new donor registrations...");

        // Get the reset time
        Instant resetTime = shouldResetStatistics();

        // Get the current time
        Instant now = Instant.now();

        // Calculate the start time for the last 24 hours
        Instant startTime = resetTime != null ? resetTime : now;
        Instant endTime = now;

        // Get the number of new donor registrations
        Long newDonorRegistrations = donationRepository.countDistinctDonorsByCreatedAtBetween(Date.from(startTime), Date.from(endTime));

        if (newDonorRegistrations == null) {
            newDonorRegistrations = 0L;
            log.info("New donor registrations was null, set to 0.");
        }

        // Create a new Statistic object
        var newStatistic = Statistic.builder()
                .id(UUID.randomUUID().toString())
                .statisticType(StatisticType.NEW_DONOR_REGISTRATIONS)
                .createdAt(now)
                .value(newDonorRegistrations.doubleValue())
                .build();

        log.info("Saving new statistic: {}", newStatistic);

        // Save the new statistic and return it
        return statisticRepository.save(newStatistic);
    }
    
    // Check if the existing statistic matches the filter
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
