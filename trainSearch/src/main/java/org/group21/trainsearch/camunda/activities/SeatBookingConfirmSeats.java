package org.group21.trainsearch.camunda.activities;
import lombok.extern.slf4j.*;
import org.camunda.bpm.engine.delegate.*;
import org.camunda.bpm.engine.impl.context.Context;
import org.group21.trainsearch.camunda.workflows.*;
import org.group21.trainsearch.model.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

@Slf4j
@Component
public class SeatBookingConfirmSeats implements JavaDelegate {
    private final RestTemplate restTemplate;

    public SeatBookingConfirmSeats(RestTemplate restTemplate) {
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

        Route route = (Route) execution.getVariable(TicketOrderWorkflow.VARIABLE_ROUTE);
        if (route == null || route.getJourneys() == null) {
            String errorMsg = "No route or journeys found in execution variables for confirmation.";
            log.error(errorMsg);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        for (Journey journey : route.getJourneys()) {
            String operatorUrl = journey.getOperator().getUrl();
            String confirmEndpoint = operatorUrl + "/journey/" + journey.getId() + "/confirm";
            ResponseEntity<Void> response = restTemplate.postForEntity(confirmEndpoint, null, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorMsg = "Failed to confirm seat for journey id: " + journey.getId();
                log.error(errorMsg);
                execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
                throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
            }
        }
        log.info("Successfully confirmed seats for all journeys.");
    }
}
