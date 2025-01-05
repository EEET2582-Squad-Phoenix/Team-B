package com.teamb.charity.controllers;

import com.teamb.charity.models.Continent;
import com.teamb.charity.services.ContinentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/continent")
public class ContinentController {

    private final ContinentService continentService;

    @GetMapping
    public ResponseEntity<List<Continent>> getAllContinents(
            @RequestParam String country,
            @RequestParam String continent
    ) {
        return ResponseEntity.ok(continentService.findAllContinents(country, continent));
    }

    @GetMapping("/{continentId}")
    public ResponseEntity<Continent> getContinentById(@NotNull @PathVariable String continentId) {
        return ResponseEntity.ok(continentService.getContinentById(continentId));
    }

    @PostMapping
    public ResponseEntity<Continent> createContinent(@Valid @RequestBody Continent continent) {
        return new ResponseEntity<>(continentService.createContinent(continent), HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<Continent> updateContinent(@Valid @RequestBody Continent continent) {
        return ResponseEntity.ok(continentService.updateContinent(continent));
    }

    @DeleteMapping("/{continentId}")
    public ResponseEntity<ProblemDetail> deleteContinent(@NotNull @PathVariable String continentId) {
        continentService.deleteContinent(continentId);
        ProblemDetail deletedMsg = ProblemDetail.forStatus(HttpStatus.OK);
        deletedMsg.setTitle("Continent deleted successfully");
        deletedMsg.setDetail(String.format("Continent with id %s deleted successfully", continentId));
        return ResponseEntity.ok(deletedMsg);
    }

}
