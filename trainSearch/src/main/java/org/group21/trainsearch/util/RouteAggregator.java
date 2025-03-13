package org.group21.trainsearch.util;

import org.group21.trainsearch.model.*;

import java.util.*;

public class RouteAggregator {

    // For simplicity, this implementation is just a placeholder.
    // In a real-world scenario, you'd build all valid journey combinations that start at departureStation
    // and end at arrivalStation, not exceeding maxChanges.
    public static List<Route> aggregateRoutes(List<Journey> journeys, String departureStation, String arrivalStation, int maxChanges) {
        List<Route> routes = new ArrayList<>();
        // TODO: Implement route combination logic
        // For demonstration, we return a single route if we have at least one journey that meets the criteria.
        // You can iterate over journeys, check if you can connect journeys (e.g., arrival time of one is before departure time of the next),
        // and build valid combinations.
        if (!journeys.isEmpty()) {
            // Example: Create a dummy route using all journeys (adjust as needed)
            routes.add(new Route(journeys));
        }
        return routes;
    }
}