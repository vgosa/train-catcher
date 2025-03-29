package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.group21.trainsearch.camunda.workflows.TicketOrderWorkflow;
import org.group21.trainsearch.config.JMSProducer;
import org.group21.trainsearch.model.Route;
import org.group21.trainsearch.model.Ticket;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailSendTicket implements JavaDelegate {

    private final JMSProducer jmsProducer;

    private final ObjectMapper objectMapper;

    private static final String EMAIL_QUEUE = "ticket";

    public EmailSendTicket(JMSProducer jmsProducer, ObjectMapper objectMapper) {
        this.jmsProducer = jmsProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        Ticket ticket;
        Route route;
        try {
            ticket = (Ticket) execution.getVariableTyped("ticket").getValue();
            route = (Route) execution.getVariableTyped("route").getValue();
        } catch (IllegalArgumentException e) {
            String errorMsg = "Failed to convert ticket or route from execution variables";
            log.error(errorMsg, e);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        if (ticket == null || route == null) {
            String errorMsg = "Ticket or route is null";
            log.error(errorMsg);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        JSONObject ticketJson = new JSONObject()
                .put("passenger", "Vlad Gosa") //TODO: ticket.getPassenger()
                .put("email", "v.gosa@student.utwente.nl") //TODO: ticket.getEmail()
                .put("journeys", route.getJourneys())
                .put("totalPrice", route.getTotalPrice())
                .put("totalDuration", route.getTotalDuration());
        log.info("Successfully parsed ticket from execution variables. Sending ticket via email.");

        jmsProducer.sendMessage(ticketJson.toString(), EMAIL_QUEUE);
    }
}
