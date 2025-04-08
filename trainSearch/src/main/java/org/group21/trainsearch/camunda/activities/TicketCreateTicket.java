package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.group21.trainsearch.camunda.workflows.TicketOrderWorkflow;
import org.group21.trainsearch.model.Ticket;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class TicketCreateTicket implements JavaDelegate {



    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public TicketCreateTicket(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        if (Context.getJobExecutorContext().getCurrentJob().getRetries() <= 1) {
            String errorMsg = "Failed to create ticket. No more retries left.";
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        Long bookingId = (Long) execution.getVariableTyped(TicketOrderWorkflow.VARIABLE_BOOKING_ID).getValue();
        Long userId = (Long) execution.getVariableTyped(TicketOrderWorkflow.VARIABLE_USER_ID).getValue();
        boolean isValid = true;

        if (bookingId == null || userId == null) {
            String errorMsg = "Booking ID or User ID is null. Cannot create a ticket!";
            log.error(errorMsg);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        Ticket ticket = new Ticket(userId, bookingId, isValid);

        log.info("Contacting the ticket service to create a ticket.");

        ResponseEntity<String> response = restTemplate.postForEntity(TicketOrderWorkflow.TICKET_SERVICE_URL, ticket, String.class);

        if (response.getStatusCode().isError()) {
            String errorMsg = String.format("Failed to create ticket. Ticket service returned status code %s", response.getStatusCode());
            log.error(errorMsg);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        // Successfully received response from ticket service. Getting the ticket from the response.
        String ticketString = response.getBody();

        try {
            ticket = objectMapper.readValue(ticketString, Ticket.class);
        } catch (Exception e) {
            String errorMsg = "Failed to parse ticket ID from ticket service response";
            log.error(errorMsg, e);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        execution.setVariable(TicketOrderWorkflow.VARIABLE_TICKET, ticket);
        execution.setVariable(TicketOrderWorkflow.VARIABLE_TICKET_ID, ticket.getId());
        log.info("Successfully created ticket with ID {}", ticket);
    }
}
