package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.group21.trainsearch.camunda.workflows.TicketPaymentWorkflow;
import org.group21.trainsearch.model.Journey;
import org.group21.trainsearch.model.Operator;
import org.group21.trainsearch.model.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PaymentProcessPayment implements JavaDelegate {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private final TicketPaymentWorkflow ticketPaymentWorkflow;

    public PaymentProcessPayment(ObjectMapper objectMapper, RestTemplate restTemplate, TicketPaymentWorkflow ticketPaymentWorkflow) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.ticketPaymentWorkflow = ticketPaymentWorkflow;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        if (Context.getJobExecutorContext().getCurrentJob().getRetries() <= 1) {
            String errorMsg = "Failed to process payments. No more retries left.";
            log.error(errorMsg);
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }

        Long userId = (Long) execution.getVariable(TicketPaymentWorkflow.VARIABLE_USER_ID);

        if (userId == null) {
            String errorMsg = "User ID is null";
            log.error(errorMsg);
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }

        log.info("Processing payment for user with ID {}", userId);

        Route route;
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

        ResponseEntity<Double> userResponse = restTemplate.postForEntity(String.format("%s/%s/deduct",
                TicketPaymentWorkflow.USER_SERVICE_URL, userId), route.getTotalPrice(), Double.class);

        if (userResponse.getStatusCode().isError()) {
            String errorMsg = "Failed to deduct payment from user";
            log.error(errorMsg);
            execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
        }

        Map<Operator, Double> operators = route.getJourneys().stream()
                .collect(Collectors.groupingBy(Journey::getOperator, Collectors.summingDouble(Journey::getPrice)));

        for (Map.Entry<Operator, Double> entry : operators.entrySet()) {
            Operator operator = entry.getKey();
            Double amount = entry.getValue();
            log.info("Processing payment for operator {} with amount {}", operator, amount);
            ResponseEntity<Operator> operatorResponse =  restTemplate.postForEntity(String.format("%s/%s/%s/topup",
                            operator.getUrl(), TicketPaymentWorkflow.OPERATOR_CONTEXT_PATH, operator.getName()),
                    amount,
                    Operator.class);

            if (operatorResponse.getStatusCode().isError()) {
                String errorMsg = String.format("Failed to process payment for operator %s", operator.getName());
                log.error(errorMsg);
                execution.setVariable(TicketPaymentWorkflow.FAILURE_REASON, errorMsg);
                throw new BpmnError(TicketPaymentWorkflow.DO_NOT_RETRY, errorMsg);
            }
            TicketPaymentWorkflow.PaymentJob paymentJob = new TicketPaymentWorkflow.PaymentJob(
                    operator.getName(), amount, operator.getUrl()
            );
            ticketPaymentWorkflow.addPaymentJob(paymentJob);
        }

        log.info("Successfully processed payment for user with ID {}", userId);
    }
}
