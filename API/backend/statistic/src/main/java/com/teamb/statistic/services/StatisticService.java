package com.teamb.statistic.services;

import com.teamb.common.models.ProjectCategoryType;
import com.teamb.common.models.ProjectStatus;
import com.teamb.statistic.models.Statistic;
import com.teamb.statistic.models.StatisticType;

import com.teamb.statistic.repositories.StatisticRepository;
import com.teamb.charity_projects.repositories.CharityProjectRepository;
import com.teamb.donation.repositories.DonationRepository;
import com.teamb.account.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
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
    // Store the time zone of London (UTC+0)
    private final ZoneId resetTime = ZoneId.of("Europe/London");

    private static final Integer MAXIMUM_ACCEPTABLE_OUTDATED_HRS = 4;

    private static final Function<FluentQuery.FetchableFluentQuery<Statistic>, Optional<Statistic>> GET_FIRST_ORDER_DESC_BY_CREATED_AT = (
            q) -> q.sortBy(Sort.by("createdAt").descending()).first();

    private final StatisticRepository statisticRepository;

    private final CharityProjectRepository charityProjectRepository;

    private final DonationRepository donationRepository;
    private final AccountRepository accountRepository;

    public Instant ResetStatistics() {
        // Get the current time in UTC
        Instant now = Instant.now();

        // Extract the hour, minute, and second in UTC
        int resetTimeNowHour = now.atZone(ZoneOffset.UTC).getHour();
        int resetTimeNowMinute = now.atZone(ZoneOffset.UTC).getMinute();
        int resetTimeNowSecond = now.atZone(ZoneOffset.UTC).getSecond();

        // Check if the hour is a multiple of 4 and the minute and second are exactly 0
        if (resetTimeNowMinute == 0 && resetTimeNowSecond == 0 && resetTimeNowHour % 4 == 0) {
            return ZonedDateTime.ofInstant(now, ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS).withHour(resetTimeNowHour)
                    .toInstant(); // Return the exact reset time
        }

        // If it's not time to reset, return null
        return null;
    }

    // Create a new statistic every 4 hours
    // Create a new statistic every 4 hours
    public boolean shouldResetStatistics() {
        // Define the London time zone
        ZoneId londonTimeZone = ZoneId.of("Europe/London");

        // Get the current time in UTC and convert it to the London time zone
        Instant now = Instant.now();
        ZonedDateTime resetTimeNow = now.atZone(londonTimeZone); // Convert UTC to London time

        // Extract the hour, minute, and second
        int resetTimeNowHour = resetTimeNow.getHour();
        int resetTimeNowMinute = resetTimeNow.getMinute();
        int resetTimeNowSecond = resetTimeNow.getSecond();

        // Check if the hour is a multiple of 4 and the minute and second are exactly 0
        return resetTimeNowHour % 4 == 0 && resetTimeNowMinute == 0 && resetTimeNowSecond == 0;
    }

    // ! Every 4 hours, create a new stat object.
    // ! If the stats object is not found, create a new one.
    // ! Fetch lastest statistics from the database. If it reaches 4 hours, create a
    // new one. Else, return the existing one.

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

        boolean shouldReset = shouldResetStatistics();

        // If statistic does not exist or it’s time to reset, create a new statistic
        if (statistic == null || shouldReset) {
            log.info("Statistic not found or reset is due. Creating a new statistic.");
            statistic = Statistic.builder()
                    .id(UUID.randomUUID().toString())
                    .userTargetIDs(List.of(userTargetID))
                    .statisticType(StatisticType.DONATION_VALUE_USER)
                    .createdAt(Instant.now())
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
                Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase().withIgnorePaths("value")),
                GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(null);

        log.info("Latest statistic found: {}", statistic);

        boolean shouldReset = shouldResetStatistics();

        // If statistic does not exist or it’s time to reset, create a new statistic
        if (statistic == null || shouldReset) {
            log.info("Statistic not found or reset is due. Creating a new statistic.");
            statistic = Statistic.builder()
                    .id(UUID.randomUUID().toString())
                    .userTargetIDs(List.of(userTargetID))
                    .statisticType(StatisticType.PROJECT_COUNT_USER)
                    .createdAt(Instant.now())
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

        Statistic statistic = statisticRepository
                .findBy(
                        Example.of(filter, ExampleMatcher.matching().withIgnoreCase().withIgnorePaths("value")),
                        GET_FIRST_ORDER_DESC_BY_CREATED_AT)
                .orElse(null);

        boolean shouldReset = shouldResetStatistics();
        // Create a new Statistic object if no matching record exists or reset is due
        log.info("No existing statistic found or reset is due. Creating new statistic...");
        if (statistic == null || shouldReset) {

            statistic = Statistic.builder()
                    .id(UUID.randomUUID().toString())
                    .statisticType(StatisticType.DONATION_VALUE_SYSTEM)
                    .filterCategory(filter.getFilterCategory())
                    .filterContinent(filter.getFilterContinent())
                    .filterCountry(filter.getFilterCountry())
                    .filterStatus(filter.getFilterStatus())
                    .filterStartDate(filter.getFilterStartDate())
                    .filterEndDate(filter.getFilterEndDate())
                    .build();
        }

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
        statistic.setValue(totalDonationValue);
        statistic.setCreatedAt(Instant.now());

        log.info("Saving new statistic: {}", statistic);

        return statisticRepository.save(statistic);
    }

    // Calculate total project count using the filter
    public Statistic calculateProjectCount(Statistic filter) {
        log.info("Filter values received: {}", filter);

        Statistic statistic = statisticRepository
                .findBy(
                        Example.of(filter, ExampleMatcher.matching().withIgnoreCase().withIgnorePaths("value")),
                        GET_FIRST_ORDER_DESC_BY_CREATED_AT)
                .orElse(null);

        boolean shouldReset = shouldResetStatistics();
        // Create a new Statistic object if no matching record exists or reset is due
        log.info("No existing statistic found or reset is due. Creating new statistic...");
        if (statistic == null || shouldReset) {

            statistic = Statistic.builder()
                    .id(UUID.randomUUID().toString())
                    .statisticType(StatisticType.DONATION_VALUE_SYSTEM)
                    .filterCategory(filter.getFilterCategory())
                    .filterContinent(filter.getFilterContinent())
                    .filterCountry(filter.getFilterCountry())
                    .filterStatus(filter.getFilterStatus())
                    .filterStartDate(filter.getFilterStartDate())
                    .filterEndDate(filter.getFilterEndDate())
                    .build();
        }

        // Calculate the total project count
        Long totalProjectCount = charityProjectRepository
                .countAllByCategoriesContainingAndContinentMatchesRegexAndCountryMatchesRegexAndStatusInAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                        filter.getFilterCategory(),
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
        statistic.setValue(totalProjectCount.doubleValue());
        statistic.setCreatedAt(Instant.now());

        log.info("Saving new statistic: {}", statistic);

        return statisticRepository.save(statistic);
    }

    public Statistic calculateNewDonorRegistrations() {
        log.info("Calculating new donor registrations...");

        // Create a base Statistic object for searching
        var baseValue = Statistic.builder()
                .statisticType(StatisticType.NEW_DONOR_REGISTRATIONS)
                .build();

        // Always fetch the latest statistic from the database
        var statistic = statisticRepository.findBy(
                Example.of(baseValue, ExampleMatcher.matching().withIgnoreCase().withIgnorePaths("value")),
                GET_FIRST_ORDER_DESC_BY_CREATED_AT).orElse(null);

        log.info("Latest statistic found: {}", statistic);

        boolean shouldReset = shouldResetStatistics();

        // If statistic does not exist or it’s time to reset, create a new statistic
        if (statistic == null || shouldReset) {
            log.info("Statistic not found or reset is due. Creating a new statistic.");

            // Get the reset time
            Instant resetTime = ResetStatistics();

        // Get the current time truncated to the hour
        Instant now = Instant.now().truncatedTo(ChronoUnit.HOURS);

        // Calculate the start time for the last 24 hours truncated to the hour
        Instant startTime = resetTime != null ? resetTime.truncatedTo(ChronoUnit.HOURS) : now.minus(24, ChronoUnit.HOURS);
        Instant endTime = now;

            // Get the number of new donor registrations
            long newDonorRegistrations = accountRepository.countDistinctDonorsByCreatedAtBetween(Date.from(startTime), Date.from(endTime));

            log.info("New donor registrations: {}", newDonorRegistrations);

            // Create a new Statistic object
            statistic = Statistic.builder()
                    .id(UUID.randomUUID().toString())
                    .statisticType(StatisticType.NEW_DONOR_REGISTRATIONS)
                    .createdAt(now)
                    .value((double) newDonorRegistrations)
                    .build();

            log.info("Saving new statistic: {}", statistic);

            // Save the new statistic and return it
            return statisticRepository.save(statistic);
        }

        log.info("Returning existing statistic: {}", statistic);
        return statistic;
    }

}