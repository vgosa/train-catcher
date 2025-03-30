package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.*;
import org.camunda.bpm.engine.delegate.*;
import org.group21.trainsearch.camunda.workflows.*;
import org.group21.trainsearch.model.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

@Slf4j
@Component
public class SeatBookingBlockSeats implements JavaDelegate{

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public SeatBookingBlockSeats(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Entering SeatBookingBlockSeats delegate.");
        Route route;
        try {
            route = objectMapper.convertValue(execution.getVariable(TicketOrderWorkflow.VARIABLE_ROUTE), Route.class);
        } catch (IllegalArgumentException e) {
            String errorMsg = "Failed to convert route from execution variables";
            log.error(errorMsg, e);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }
        if (route == null) {
            String errorMsg = "Route is null";
            log.error(errorMsg);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
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
