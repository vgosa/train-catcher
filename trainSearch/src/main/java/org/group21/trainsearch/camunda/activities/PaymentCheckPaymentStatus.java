package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.group21.trainsearch.camunda.workflows.TicketOrderWorkflow;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class PaymentCheckPaymentStatus implements JavaDelegate {

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public PaymentCheckPaymentStatus(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        String failureReason = (String) execution.getVariable(TicketOrderWorkflow.FAILURE_REASON);
        if (failureReason != null) {
            log.error("Payment failed: " + failureReason);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, failureReason);
        }
    }
}
