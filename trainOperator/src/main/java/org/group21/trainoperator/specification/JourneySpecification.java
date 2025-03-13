package org.group21.trainoperator.specification;

import org.group21.trainoperator.model.Journey;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class JourneySpecification {

    public static final String DEPARTURE_STATION = "departureStation";
    public static final String ARRIVAL_STATION = "arrivalStation";
    public static final String DEPARTURE_TIME = "departureTime";

    // Private constructor prevents instantiation
    private JourneySpecification() {
        // empty
    }

    // Filter method that combines all criteria
    public static Specification<Journey> filterBy(JourneyFilter filter) {
        return Specification
                .where(hasDepartureStation(filter.getDepartureStation()))
                .and(hasArrivalStation(filter.getArrivalStation()))
                .and(hasDepartureTime(filter.getDepartureTime()));
    }

    private static Specification<Journey> hasDepartureStation(String departureStation) {
        return (root, query, cb) ->
                (departureStation == null || departureStation.isEmpty())
                        ? cb.conjunction()
                        : cb.equal(root.get(DEPARTURE_STATION), departureStation);
    }

    private static Specification<Journey> hasArrivalStation(String arrivalStation) {
        return (root, query, cb) ->
                (arrivalStation == null || arrivalStation.isEmpty())
                        ? cb.conjunction()
                        : cb.equal(root.get(ARRIVAL_STATION), arrivalStation);
    }

    private static Specification<Journey> hasDepartureTime(LocalDateTime departureTime) {
        return (root, query, cb) ->
                (departureTime == null)
                        ? cb.conjunction()
                        : cb.equal(root.get(DEPARTURE_TIME), departureTime);
    }
}
