package org.group21.trainsearch.controller;

import com.auth0.jwt.interfaces.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.group21.annotations.RequiresAuthentication;
import org.group21.exception.UnauthorizedException;
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

    public TrainSearchController(TrainSearchService trainSearchService, RestTemplate restTemplate) {
        this.trainSearchService = trainSearchService;
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

    @PostMapping("/order")
    @RequiresAuthentication
    public ResponseEntity<String> orderTicket(
            @RequestBody @Valid Route route,
            @RequestParam("userId") @Min(0) Long userId,
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            DecodedJWT decoded) {

        Long tokenUserId = decoded.getClaim("userId").asLong();
        if (!userId.equals(tokenUserId)) {
            throw new UnauthorizedException("User ID in token does not match requested user ID");
        }

        trainSearchService.orderTicket(userId, route);
        return ResponseEntity.ok("Booking request sent successfully");
    }

    @PostMapping("/camunda-test")
    @RequiresAuthentication
    public ResponseEntity<String> issuePayment(
            @RequestBody @Valid Route route,
            @RequestParam("userId") @Min(0) Long userId,
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            DecodedJWT decoded) {
        Long tokenUserId = decoded.getClaim("userId").asLong();
        if (!userId.equals(tokenUserId)) {
            throw new UnauthorizedException("User ID in token does not match requested user ID");
        }

        trainSearchService.camundaTest(userId, route);
        return ResponseEntity.ok("Payment request sent successfully");
    }
}