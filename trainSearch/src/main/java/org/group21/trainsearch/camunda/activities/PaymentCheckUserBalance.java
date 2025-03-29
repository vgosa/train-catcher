package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.group21.trainsearch.camunda.workflows.TicketPaymentWorkflow;
import org.group21.trainsearch.model.Route;
import org.group21.trainsearch.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class PaymentCheckUserBalance implements JavaDelegate {

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public PaymentCheckUserBalance(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        if(Context.getJobExecutorContext().getCurrentJob().getRetries() <= 1) {
            String errorMsg = "Failed to check user balance. No more retries left.";
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }

        Long userId = (Long) execution.getVariable(TicketPaymentWorkflow.VARIABLE_USER_ID);
        Route route;

        if (userId == null) {
            String errorMsg = "User ID is null";
            log.error(errorMsg);
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }

        try {
            route = objectMapper.convertValue(execution.getVariable(TicketPaymentWorkflow.VARIABLE_ROUTE), Route.class);
        } catch (IllegalArgumentException e) {
            String errorMsg = "Failed to convert route from execution variables";
            log.error(errorMsg, e);
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }
        if (route == null) {
            String errorMsg = "Route is null";
            log.error(errorMsg);
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }

        ResponseEntity<User> response = restTemplate.getForEntity(TicketPaymentWorkflow.USER_SERVICE_URL + "/" + userId, User.class);

        if (response.getStatusCode().isError()) {
            String errorMsg = "Failed to get user from user service";
            log.error(errorMsg);
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }

        User user = response.getBody();

        if (user == null) {
            String errorMsg = "User is null";
            log.error(errorMsg);
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }

        if (user.getBalance() < route.getTotalPrice()) {
            log.error("User does not have sufficient balance to pay for the ticket");
            execution.setVariable(TicketPaymentWorkflow.VARIABLE_SUFFICIENT_BALANCE, false);
        } else {
            log.info("User has sufficient balance to pay for the ticket");
            execution.setVariable(TicketPaymentWorkflow.VARIABLE_SUFFICIENT_BALANCE, true);
        }
    }
}
