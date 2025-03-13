package org.group21.trainsearch.service;

import org.group21.trainsearch.model.*;
import org.group21.trainsearch.util.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

import java.time.*;
import java.util.*;

@Service
public class TrainSearchService {

    private final OperatorService operatorService;
    private final RestTemplate restTemplate; // or use WebClient for async calls

    public TrainSearchService(OperatorService operatorService) {
        this.operatorService = operatorService;
        this.restTemplate = new RestTemplate();
    }

    public List<Route> searchRoutes(String departureStation, String arrivalStation, LocalDateTime departureTime, int maxChanges) {
        List<Journey> allJourneys = new ArrayList<>();

        // Query each operator's journey search API
        for (Operator operator : operatorService.getOperators()) {
            // Build the URL, e.g., operator.getUrl() + "/journey?departure_station=...&arrival_station=..."
            String url = operator.getUrl() + "/journey?departure_station=" + departureStation
                    + "&arrival_station=" + arrivalStation
                    + "&departure_time=" + departureTime.toString();
            try {
                // Assume each operator returns an array of Journey DTOs
                Journey[] journeys = restTemplate.getForObject(url, Journey[].class);
                if (journeys != null) {
                    for (Journey journey : journeys) {
                        allJourneys.add(journey);
                    }
                }
            } catch (Exception e) {
                // Log error, skip operator if unreachable
                System.err.println("Error querying operator " + operator.getUrl() + ": " + e.getMessage());
            }
        }

        // Aggregate journeys into possible routes based on allowed train changes
        // This logic is domain-specific and can be as complex as needed.
        List<Route> routes = RouteAggregator.aggregateRoutes(allJourneys, departureStation, arrivalStation, maxChanges);
        return routes;
    }
}