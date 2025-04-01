package org.group21.trainsearch.service;

import lombok.extern.slf4j.Slf4j;
import org.group21.trainsearch.camunda.workflows.TicketOrderWorkflow;
import org.group21.trainsearch.camunda.workflows.TicketPaymentWorkflow;
import org.group21.trainsearch.model.*;
import org.group21.trainsearch.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

import java.time.*;
import java.util.*;

@Service
@Slf4j
public class TrainSearchService {

    private final OperatorService operatorService;

    private final TicketOrderWorkflow ticketOrderWorkflow;

    private final TicketPaymentWorkflow ticketPaymentWorkflow;
    private final RestTemplate restTemplate; // You might switch to WebClient for asynchronous calls

    public TrainSearchService(OperatorService operatorService, TicketOrderWorkflow ticketOrderWorkflow,
                              RestTemplate restTemplate, TicketPaymentWorkflow ticketPaymentWorkflow) {
        this.operatorService = operatorService;
        this.ticketOrderWorkflow = ticketOrderWorkflow;
        this.restTemplate = restTemplate;
        this.ticketPaymentWorkflow = ticketPaymentWorkflow;
    }

    /**
     * Queries all registered operator services (without applying filters) to obtain journeys.
     * For each journey, sets the operator field to the source operator.
     * Then, aggregates the journeys into possible routes.
     *
     * @param departureStation the starting station (used later for route aggregation)
     * @param arrivalStation   the final destination (used later for route aggregation)
     * @param departureTime    the desired departure time (currently not used in aggregation logic)
     * @param maxChanges       maximum allowed train changes (i.e. max segments - 1)
     * @return list of aggregated routes
     */
    public List<Route> searchRoutes(String departureStation, String arrivalStation, LocalDateTime departureTime, int maxChanges) {
        List<Journey> allJourneys = new ArrayList<>();

        for (Operator operator : operatorService.getOperators()) {
            String url = operator.getUrl() + OperatorService.OPERATOR_JOURNEY_ENDPOINT; // no filters appended
            try {
                Journey[] journeys = restTemplate.getForObject(url, Journey[].class);
                for (Journey journey : Objects.requireNonNull(journeys)) {
                    journey.setOperator(operator);
                    allJourneys.add(journey);
                }
            } catch (RestClientException e) {
                log.error("Error querying operator " + operator.getUrl() + ": " + e.getMessage());
                throw e;
            }
        }

        return RouteAggregator.aggregateRoutes(allJourneys, departureStation, arrivalStation, departureTime, maxChanges);
    }

    public void orderTicket(long userId, Route route) {
        ticketOrderWorkflow.startTicketOrderWorkflow(userId, route);
    }

    public void camundaTest(long userId, Route route) {
        ticketPaymentWorkflow.startTicketPaymentWorkflow(userId, route);
    }
}