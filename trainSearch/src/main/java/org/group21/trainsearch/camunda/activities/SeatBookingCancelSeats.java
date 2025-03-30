package org.group21.trainsearch.camunda.activities;

import lombok.extern.slf4j.*;
import org.camunda.bpm.engine.delegate.*;
import org.group21.trainsearch.camunda.workflows.*;
import org.group21.trainsearch.model.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

@Slf4j
@Component
public class SeatBookingCancelSeats implements JavaDelegate {
    private final RestTemplate restTemplate;

    public SeatBookingCancelSeats(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Route route = (Route) execution.getVariable(TicketOrderWorkflow.VARIABLE_ROUTE);
        if (route == null || route.getJourneys() == null) {
            String errorMsg = "No route or journeys found in execution variables for cancellation.";
            log.error(errorMsg);
            throw new BpmnError("CancelSeatsError", errorMsg);
        }

        for (Journey journey : route.getJourneys()) {
            String operatorUrl = journey.getOperator().getUrl();
            String cancelEndpoint = operatorUrl + "/journey/" + journey.getId() + "/cancel";
            ResponseEntity<Void> response = restTemplate.postForEntity(cancelEndpoint, null, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorMsg = "Failed to cancel seat for journey id: " + journey.getId();
                log.error(errorMsg);
                throw new BpmnError("CancelSeatsError", errorMsg);
            }
        }
        log.info("Successfully canceled seats for all journeys.");
    }
}
