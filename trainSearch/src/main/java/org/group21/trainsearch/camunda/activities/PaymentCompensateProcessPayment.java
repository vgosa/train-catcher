package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.group21.trainsearch.camunda.workflows.TicketPaymentWorkflow;
import org.group21.trainsearch.model.Operator;
import org.group21.trainsearch.model.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Deque;

@Component
@Slf4j
public class PaymentCompensateProcessPayment implements JavaDelegate {

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    private final TicketPaymentWorkflow ticketPaymentWorkflow;

    public PaymentCompensateProcessPayment(ObjectMapper objectMapper, RestTemplate restTemplate, TicketPaymentWorkflow ticketPaymentWorkflow) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.ticketPaymentWorkflow = ticketPaymentWorkflow;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        if (ticketPaymentWorkflow.getPaymentJobs().isEmpty()) {
            log.info("User was not credited. No compensation is required.");
        }

        Long userId = (Long) execution.getVariable(TicketPaymentWorkflow.VARIABLE_USER_ID);
        Route route = objectMapper.convertValue(execution.getVariable(TicketPaymentWorkflow.VARIABLE_ROUTE), Route.class);

        log.info("Compensating payment processing for user with ID {}", userId);

        try {
            ResponseEntity<Double> userResponse = restTemplate.postForEntity(String.format("%s/%s/topup",
                    TicketPaymentWorkflow.USER_SERVICE_URL, userId), route.getTotalPrice(), Double.class);

            if (userResponse.getStatusCode().isError()) {
                String errorMsg = "Failed to compensate payment processing. CAUSE: Something went wrong in the user service.";
                log.error(errorMsg);
                execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
                throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Failed to compensate payment processing. CAUSE: Could not contact the user service to credit the user.";
            log.error(errorMsg, e);
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }
        log.info("Successfully compensated payment processing for user with ID {}", userId);

        Deque<TicketPaymentWorkflow.PaymentJob> paymentJobs = ticketPaymentWorkflow.getPaymentJobs();

        if (paymentJobs.isEmpty()) {
            log.info("No payment jobs found in the context. No further compensation is required.");
            return;
        }

        for (int i = 0; i < paymentJobs.size(); i++) {
            TicketPaymentWorkflow.PaymentJob job = paymentJobs.pop();
            log.info("Compensating payment job for Operator {}", job.getOperatorName());

            try {
                ResponseEntity<Operator> operatorResponse =  restTemplate.postForEntity(String.format("%s/%s/%s/deduct",
                                job.getUrl(), TicketPaymentWorkflow.OPERATOR_CONTEXT_PATH, job.getOperatorName()),
                        job.getAmount(),
                        Operator.class);
                if (operatorResponse.getStatusCode().isError()) {
                    String errorMsg = "Failed to compensate payment job. CAUSE: Something went wrong in the operator service.";
                    log.error(errorMsg);
                    execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
                    throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
                }
            } catch (Exception e) {
                String errorMsg = "Failed to compensate payment job. CAUSE: Could not contact the operator service to deduct the amount.";
                log.error(errorMsg, e);
                execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
                throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
            }
            log.info("Successfully compensated payment job for Operator {}", job.getOperatorName());
        }
    }
}
