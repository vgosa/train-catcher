package org.group21.trainsearch.camunda.activities;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.group21.trainsearch.camunda.workflows.TicketOrderWorkflow;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
@Slf4j
public class PaymentCompensateIssuePayment implements JavaDelegate {

    private final RestTemplate restTemplate;

    public PaymentCompensateIssuePayment(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("{} called with {}", getClass().getTypeName(), execution.getVariables());

        Long paymentId = (Long) execution.getVariable(TicketOrderWorkflow.VARIABLE_PAYMENT_ID);
        if (paymentId == null) {
            log.info("No paymentId found in the context. No compensation is required.");
            return;
        }

        log.info("Compensating payment status for paymentId: {}", paymentId);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(TicketOrderWorkflow.PAYMENT_SERVICE_URL + "/status/" + paymentId + "?isSuccess=false",
                    HttpMethod.PUT, null, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorMessage = "Failed to set payment status to failed. CAUSE: Payment service returned status code " + response.getStatusCode();
                log.error(errorMessage);
                execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMessage);
                return;
            }
        } catch (RuntimeException e) {
            String errorMessage = "Failed to compensate payment issuing. CAUSE: Could not contact the payment service to modify the payment status.";
            log.error(errorMessage, e);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMessage);
            return;
        }

        log.info("Successfully compensated payment issuing for paymentId: {}", paymentId);
    }
}
