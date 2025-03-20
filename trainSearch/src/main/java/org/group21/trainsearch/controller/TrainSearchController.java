package org.group21.trainsearch.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.group21.trainsearch.model.*;
import org.group21.trainsearch.service.*;
import org.springframework.format.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/search")
public class TrainSearchController {

    private final TrainSearchService trainSearchService;

    private final RestTemplate restTemplate;
    public TrainSearchController(TrainSearchService trainSearchService, RestTemplate restTemplate) {
        this.trainSearchService = trainSearchService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/routes")
    public ResponseEntity<List<Route>> searchRoutes(
            @RequestParam("departure_station") String departureStation,
            @RequestParam("arrival_station") String arrivalStation,
            @RequestParam("departure_time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
            @RequestParam(value = "max_changes", defaultValue = "2") @Min(value = 0) int maxChanges) {

        List<Route> routes = trainSearchService.searchRoutes(departureStation, arrivalStation, departureTime, maxChanges);
        return ResponseEntity.ok(routes);
    }

    @PostMapping("/camunda-test")
    public ResponseEntity<Void> camundaTest(@RequestBody @Valid Route route, @RequestParam("userId") @Min(0) Long userId) {
        trainSearchService.testCamunda(userId, route);
        return ResponseEntity.ok().build();
    }
}