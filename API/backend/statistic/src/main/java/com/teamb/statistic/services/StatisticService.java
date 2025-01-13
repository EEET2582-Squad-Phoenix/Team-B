package com.teamb.statistic.services;
 
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import com.teamb.charity_projects.repositories.CharityProjectRepository;
import com.teamb.donation.repositories.DonationRepository;
import com.teamb.statistic.models.Statistic;
import com.teamb.statistic.models.StatisticType;
import com.teamb.statistic.repositories.StatisticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
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
 
    // Create a new statistic every 4 hours
    public void resetStatistics() {
        // Get the current time in UTC and convert it to the London time zone
        Instant now = Instant.now();
        ZonedDateTime resetTimeNow = now.atZone(resetTime); // Convert UTC to London time

        // Extract the hour, minute, and second
        int resetTimeNowHour = resetTimeNow.getHour();
        int resetTimeNowMinute = resetTimeNow.getMinute();
        int resetTimeNowSecond = resetTimeNow.getSecond();

        // Check if the hour is a multiple of 4 and the minute and second are exactly 0
        if (resetTimeNowHour % 4 == 0 && resetTimeNowMinute == 0 && resetTimeNowSecond == 0) {
            log.info("Resetting statistics at {}", resetTimeNow);  // Log the exact reset time

            // Total Number of Donation Value
            calculateDonationValueForOneTarget(null, false); // by CHARITY
            calculateDonationValueForOneTarget(null, true); // by DONOR
            // by filter
            // Total Number of Projects
            calculateProjectCountForOneTarget(null, false); // by CHARITY
            calculateProjectCountForOneTarget(null, true); // by DONOR
            // by status (ACTIVE/COMPLETED)
            // by filter

            // statisticRepository.deleteAll();  // Perform the reset operation
        }
    }

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
                .filterStartDate(filter.getFilterStartDate())
                .filterEndDate(filter.getFilterEndDate())
                .build();
        log.info("No existing statistic found or outdated, creating new: {}", newStatistic);
 
        // Calculate new value
        Double totalDonationValue = charityProjectRepository.sumTotalRaisedAmountBy(
                valueOrEmpty(filter.getFilterContinent()),
                valueOrEmpty(filter.getFilterCountry()),
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
                .filterStartDate(filter.getFilterStartDate())
                .filterEndDate(filter.getFilterEndDate())
                .build();
        log.info("No existing statistic found or outdated, creating new: {}", newStatistic);
 
        // Calculate new value
        Double totalProjectCount = charityProjectRepository.countBy(
                valueOrEmpty(filter.getFilterContinent()),
                valueOrEmpty(filter.getFilterCountry()),
                valueOrEmptyList(filter.getFilterCategory()));
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