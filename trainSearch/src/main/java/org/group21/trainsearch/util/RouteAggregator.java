package org.group21.trainsearch.util;

import org.group21.trainsearch.model.*;

import java.time.*;
import java.util.*;

public class RouteAggregator {

    public static List<Route> aggregateRoutes(List<Journey> journeys, String departureStation, String arrivalStation, LocalDateTime requestedDepartureTime, int maxChanges) {
        if (journeys == null || departureStation == null || arrivalStation == null || requestedDepartureTime == null || maxChanges < 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        Map<String, List<Journey>> journeyMap = new HashMap<>();
        for (Journey journey : journeys) {
            journeyMap.computeIfAbsent(journey.getDepartureStation().toLowerCase(), k -> new ArrayList<>()).add(journey);
        }

        List<Route> routes = new ArrayList<>();
        LocalTime reqTime = requestedDepartureTime.toLocalTime();

        journeyMap.getOrDefault(departureStation.toLowerCase(), Collections.emptyList()).stream()
                .filter(journey -> !journey.getDepartureTime().toLocalTime().isBefore(reqTime))
                .forEach(journey -> findRoutes(journeyMap, journey, arrivalStation, maxChanges, new ArrayList<>(), routes));

        Set<Route> uniqueRoutes = new HashSet<>(routes);
        return new ArrayList<>(uniqueRoutes);
    }

    private static void findRoutes(Map<String, List<Journey>> journeyMap, Journey currentJourney, String targetArrival, int remainingChanges, List<Journey> currentRoute, List<Route> routes) {
        currentRoute.add(currentJourney);

        if (currentJourney.getArrivalStation().equalsIgnoreCase(targetArrival)) {
            routes.add(new Route(new ArrayList<>(currentRoute)));
        } else if (remainingChanges > 0) {
            List<Journey> nextJourneys = journeyMap.get(currentJourney.getArrivalStation().toLowerCase());
            if (nextJourneys != null) {
                LocalDateTime currentArrivalTime = currentJourney.getDepartureTime().plusMinutes(currentJourney.getTravelTime());
                nextJourneys.stream()
                        .filter(nextJourney -> nextJourney.getDepartureTime().isAfter(currentArrivalTime) && !currentRoute.contains(nextJourney))
                        .forEach(nextJourney -> findRoutes(journeyMap, nextJourney, targetArrival, remainingChanges - 1, currentRoute, routes));
            }
        }

        currentRoute.remove(currentRoute.size() - 1);
    }
}