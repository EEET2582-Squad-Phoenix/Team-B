package com.teamb.statistic.controllers;

import com.teamb.statistic.models.Statistic;
import com.teamb.statistic.services.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping("/donation-value")
    public ResponseEntity<Statistic> calculateTotalDonationValue(
            @RequestParam Date filterStartDate,
            @RequestParam Date filterEndDate) {
        Statistic statistic = statisticService.calculateTotalDonationValue(filterStartDate, filterEndDate);
        return ResponseEntity.ok(statistic);
    }

    @PostMapping("/project-count")
    public ResponseEntity<Statistic> calculateProjectCount(
            @RequestBody Statistic filter) {
        Statistic statistic = statisticService.calculateProjectCount(filter);
        return ResponseEntity.ok(statistic);
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