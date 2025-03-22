package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.group21.trainsearch.camunda.TicketOrderWorkflow;
import org.group21.trainsearch.model.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class BookingCreateBooking implements JavaDelegate {

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public BookingCreateBooking(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        if (Context.getJobExecutorContext().getCurrentJob().getRetries() <= 1) {
            String errorMsg = "Failed to create booking. No more retries left.";
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

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

        log.info("Successfully parsed route from execution variables. Contacting the booking service to create a booking.");

        ResponseEntity<String> response = restTemplate.postForEntity(TicketOrderWorkflow.BOOKING_SERVICE_URL +
                String.format("?%s=%s", TicketOrderWorkflow.VARIABLE_USER_ID,
                execution.getVariableTyped(TicketOrderWorkflow.VARIABLE_USER_ID).getValue()), route, String.class);

        if (response.getStatusCode().isError()) {
            String errorMsg = String.format("Failed to create booking. Booking service returned status code %s", response.getStatusCode());
            log.error(errorMsg);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        // Successfully received response from booking service. Getting ID of the booking
        String bookingString = response.getBody();

        long bookingId;
        try {
            bookingId = objectMapper.readTree(bookingString).get("id").asLong();
        } catch (Exception e) {
            String errorMsg = "Failed to parse booking ID from booking service response";
            log.error(errorMsg, e);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        execution.setVariable(TicketOrderWorkflow.VARIABLE_BOOKING_ID, bookingId);
        log.info("Successfully created booking with ID {}", bookingId);
    }
}
