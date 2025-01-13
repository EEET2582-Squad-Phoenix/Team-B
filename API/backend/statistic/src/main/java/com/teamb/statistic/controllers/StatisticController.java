package com.teamb.statistic.controllers;

import com.teamb.common.models.ProjectCategoryType;
import com.teamb.statistic.models.Statistic;
import com.teamb.statistic.models.StatisticType;
import com.teamb.statistic.services.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j

public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping("/donation-value")
    public ResponseEntity<Statistic> calculateTotalDonationValue(
       @RequestParam StatisticType statisticType,
            @RequestParam(required = false) List<String> filterContinents,
            @RequestParam(required = false) List<String> filterCountries,
            @RequestParam(required = false) List<ProjectCategoryType> filterCategories,
            @RequestParam(required = false) String filterStartDate,
            @RequestParam(required = false) String filterEndDate
    ) {
        try {
            // Build the Statistic filter object
            Statistic filter = Statistic.builder()
                    .statisticType(statisticType)
                    .filterContinents(filterContinents)
                    .filterCountries(filterCountries)
                    .filterCategories(filterCategories)
                    .filterStartDate(parseDate(filterStartDate))
                    .filterEndDate(parseDate(filterEndDate))
                    .build();

            // Calculate project count based on the filter
            Statistic statistic = statisticService.calculateTotalDonationValue(filter);

            return ResponseEntity.ok(statistic);
        } catch (Exception e) {
            log.error("Error occurred while calculating total donation", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/project-count")
    public ResponseEntity<Statistic> getProjectCount(
            @RequestParam StatisticType statisticType,
            @RequestParam(required = false) List<String> filterContinents,
            @RequestParam(required = false) List<String> filterCountries,
            @RequestParam(required = false) List<ProjectCategoryType> filterCategories,
            @RequestParam(required = false) String filterStartDate,
            @RequestParam(required = false) String filterEndDate
    ) {
        try {
            // Build the Statistic filter object
            Statistic filter = Statistic.builder()
                    .statisticType(statisticType)
                    .filterContinents(filterContinents)
                    .filterCountries(filterCountries)
                    .filterCategories(filterCategories)
                    .filterStartDate(parseDate(filterStartDate))
                    .filterEndDate(parseDate(filterEndDate))
                    .build();

            // Calculate project count based on the filter
            Statistic statistic = statisticService.calculateProjectCount(filter);

            return ResponseEntity.ok(statistic);
        } catch (Exception e) {
            log.error("Error occurred while calculating project count", e);
            return ResponseEntity.badRequest().build();
        }
    }

    private Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            return Date.from(Instant.parse(dateString));
        } catch (Exception e) {
            log.error("Error parsing date: {}", dateString, e);
            throw new IllegalArgumentException("Invalid date format. Use ISO-8601 format, e.g., 2025-01-11T10:00:00Z");
        }
    }

    @PostMapping("/donation-value/target")
    public ResponseEntity<Statistic> calculateDonationValueForOneTarget(
            @RequestParam String userTargetID,
            @RequestParam boolean isDonor) {
        Statistic statistic = statisticService.calculateDonationValueForOneTarget(userTargetID, isDonor);
        return ResponseEntity.ok(statistic);
    }

    @PostMapping("/project-count/target")
    public ResponseEntity<Statistic> calculateProjectCountForOneTarget(
            @RequestParam String userTargetID,
            @RequestParam boolean isDonor) {
        Statistic statistic = statisticService.calculateProjectCountForOneTarget(userTargetID, isDonor);
        return ResponseEntity.ok(statistic);
    }

}