package com.teamb.statistic.controllers;
 
import com.teamb.common.models.ProjectCategoryType;
import com.teamb.common.models.ProjectStatus;
import com.teamb.statistic.models.Statistic;
import com.teamb.statistic.models.StatisticType;
import com.teamb.statistic.services.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Array;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
 
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j
 
public class StatisticController {

    private static final String DEFAULT_REGEX_FOR_MATCHING_ALL = ".+";
    private static final Instant DEFAULT_START_DATE = Instant.EPOCH;
    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(Long.MAX_VALUE);
    private final StatisticService statisticService;
 
    @GetMapping("/donation-value")
    public ResponseEntity<Statistic> calculateTotalDonationValue(
            @RequestParam(required = false, defaultValue = "") List<ProjectCategoryType> filterCategory,
            @RequestParam(required = false, defaultValue = DEFAULT_REGEX_FOR_MATCHING_ALL) String filterContinent,
            @RequestParam(required = false, defaultValue = DEFAULT_REGEX_FOR_MATCHING_ALL) String filterCountry,
            @RequestParam(required = false, defaultValue = "") List<ProjectStatus> filterStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date filterStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date filterEndDate) {
        try {
            Date defaultStartDate = Date.from(DEFAULT_START_DATE);
            Date defaultEndDate = Date.from(DEFAULT_END_DATE);
            // Build the Statistic filter object
            Statistic filter = Statistic.builder()
                    .filterCategory(filterCategory.isEmpty()
                            ? List.of(ProjectCategoryType.values())
                            : filterCategory)
                    .filterContinent(filterContinent)
                    .filterCountry(filterCountry)
                    .filterStatus(filterStatus.isEmpty()
                            ? List.of(ProjectStatus.values())
                            : filterStatus)
                    .filterStartDate(filterStartDate != null ? filterStartDate : defaultStartDate)
                    .filterEndDate(filterEndDate != null ? filterEndDate : defaultEndDate)
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
            @RequestParam(required = false, defaultValue = "") List<ProjectCategoryType> filterCategory,
            @RequestParam(required = false, defaultValue = DEFAULT_REGEX_FOR_MATCHING_ALL) String filterContinent,
            @RequestParam(required = false, defaultValue = DEFAULT_REGEX_FOR_MATCHING_ALL) String filterCountry,
            @RequestParam(required = false, defaultValue = "") List<ProjectStatus> filterStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date filterStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date filterEndDate) {
        try {
            Date defaultStartDate = Date.from(DEFAULT_START_DATE);
            Date defaultEndDate = Date.from(DEFAULT_END_DATE);
            // Build the Statistic filter object
            Statistic filter = Statistic.builder()
                    .filterCategory(filterCategory.isEmpty()
                            ? List.of(ProjectCategoryType.values())
                            : filterCategory)
                    .filterContinent(filterContinent)
                    .filterCountry(filterCountry)
                    .filterStatus(filterStatus.isEmpty()
                            ? List.of(ProjectStatus.values())
                            : filterStatus)
                    .filterStartDate(filterStartDate != null ? filterStartDate : defaultStartDate)
                    .filterEndDate(filterEndDate != null ? filterEndDate : defaultEndDate)
                    .build();
 
            // Calculate project count based on the filter
            Statistic statistic = statisticService.calculateProjectCount(filter);
 
            return ResponseEntity.ok(statistic);
        } catch (Exception e) {
            log.error("Error occurred while calculating project count", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/donation-value/target")
    public ResponseEntity<Statistic> calculateDonationValueForOneTarget(
            @RequestParam String userTargetID,
            @RequestParam boolean isDonor) {
                
        Statistic statistic = statisticService.calculateDonationValueForOneTarget(userTargetID, isDonor);
        return ResponseEntity.ok(statistic);
    }
 
    @GetMapping("/project-count/target")
    public ResponseEntity<Statistic> calculateProjectCountForOneTarget(
            @RequestParam String userTargetID,
            @RequestParam boolean isDonor) {
        Statistic statistic = statisticService.calculateProjectCountForOneTarget(userTargetID, isDonor);
        return ResponseEntity.ok(statistic);
    }
 
}