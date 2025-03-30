package org.group21.trainsearch.camunda.activities;

import lombok.extern.slf4j.*;
import org.camunda.bpm.engine.delegate.*;
import org.group21.trainsearch.camunda.workflows.*;
import org.group21.trainsearch.model.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

@Slf4j
@Service
public class SeatBookingBlockSeats implements JavaDelegate{

    private final RestTemplate restTemplate;

    public SeatBookingBlockSeats(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Entering SeatBookingBlockSeats delegate.");
        Route route = (Route) execution.getVariable(TicketOrderWorkflow.VARIABLE_ROUTE);
        if (route == null || route.getJourneys() == null) {
            String errorMsg = "No route or journeys found in execution variables.";
            log.error(errorMsg);
            throw new BpmnError("BlockSeatsError", errorMsg);
        }
        for (Journey journey : route.getJourneys()) {
            String operatorUrl = journey.getOperator().getUrl();
            String blockEndpoint = operatorUrl + "/journey/" + journey.getId() + "/block";
            ResponseEntity<Void> response = restTemplate.postForEntity(blockEndpoint, null, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorMsg = "Failed to block seat for journey id: " + journey.getId();
                log.error(errorMsg);
                throw new BpmnError("BlockSeatsError", errorMsg);
            }
        }
        log.info("SeatBookingBlockSeats delegate completed successfully.");
    }
}
