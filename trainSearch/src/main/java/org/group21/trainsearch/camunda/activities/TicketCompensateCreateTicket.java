package org.group21.trainsearch.camunda.activities;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.group21.trainsearch.camunda.workflows.TicketOrderWorkflow;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class TicketCompensateCreateTicket implements JavaDelegate {

    private final RestTemplate restTemplate;

    private final BookingCompensateCreateBooking bookingCompensateCreateBooking;

    public TicketCompensateCreateTicket(RestTemplate restTemplate, BookingCompensateCreateBooking bookingCompensateCreateBooking) {
        this.restTemplate = restTemplate;
        this.bookingCompensateCreateBooking = bookingCompensateCreateBooking;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        Long ticketId = (Long) execution.getVariable(TicketOrderWorkflow.VARIABLE_TICKET_ID);
        if (ticketId == null) {
            log.info("No ticketId found in the context. No compensation is required.");
            return;
        }

        log.info("Compensating ticket creation for ticketId: {}", ticketId);

        try {
            restTemplate.delete("http://ticket/ticket/" + ticketId);
        } catch (Exception e) {
            log.error("Failed to compensate ticket creation. CAUSE: Could not contact the ticket service to delete the ticket.", e);
            return;
        }

        log.info("Successfully compensated ticket creation for ticketId: {}", ticketId);

        bookingCompensateCreateBooking.execute(execution);
    }
}
