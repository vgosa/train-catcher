package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.group21.trainsearch.camunda.workflows.*;
import org.group21.trainsearch.model.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Component
@Slf4j
public class PaymentIssuePayment implements JavaDelegate {

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public PaymentIssuePayment(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        if (Context.getJobExecutorContext().getCurrentJob().getRetries() <= 1) {
            String errorMsg = "Failed to issue payment. No more retries left.";
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }
        String paymentMethod = (String) execution.getVariableTyped(TicketOrderWorkflow.VARIABLE_PAYMENT_METHOD).getValue();
        Route route = objectMapper.convertValue(execution.getVariable(TicketOrderWorkflow.VARIABLE_ROUTE), Route.class);
        Long userId = (Long) execution.getVariableTyped(TicketOrderWorkflow.VARIABLE_USER_ID).getValue();
        Long bookingId = (Long) execution.getVariableTyped(TicketOrderWorkflow.VARIABLE_BOOKING_ID).getValue();

        if (paymentMethod == null || route == null || userId == null || bookingId == null) {
            String errorMsg = "Payment method, route, User ID or Booking ID is null. Cannot issue payment!";
            log.error(errorMsg);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        log.info("Contacting the payment service to issue payment.");

        JsonNode requestBody = objectMapper.createObjectNode()
                .put("userId", userId)
                .put("bookingId", bookingId)
                .put("payment_method", paymentMethod);
        URI paymentURL = UriBuilder.fromPath(TicketOrderWorkflow.PAYMENT_SERVICE_URL + "/" + paymentMethod)
                .queryParam("userId", userId)
                .queryParam("bookingId", bookingId)
                .build();
        ResponseEntity<String> response = restTemplate.postForEntity(paymentURL, "", String.class);

        if (response.getStatusCode().isError()) {
            String errorMsg = "Failed to issue payment. Payment service returned null response";
            log.error(errorMsg);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        // Successfully received response from payment service. Getting ID of payment
        String paymentString = response.getBody();

        long paymentId;
        try {
            paymentId = objectMapper.readTree(paymentString).get("id").asLong();
        } catch (Exception e) {
            String errorMsg = "Failed to parse payment ID from payment service response";
            log.error(errorMsg, e);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        execution.setVariable(TicketOrderWorkflow.VARIABLE_PAYMENT_ID, paymentId);
        log.info("Successfully issued payment. Payment ID: {}", paymentId);

        log.info("Starting the ticket payment process instance");
        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByKey(TicketPaymentWorkflow.PAYMENT_WORKFLOW_NAME, Map.of(
                TicketPaymentWorkflow.VARIABLE_USER_ID, userId,
                TicketPaymentWorkflow.VARIABLE_ROUTE, route,
                "businessKey", execution.getBusinessKey()
        ));
        execution.setVariable("childProcessStarted", true);
    }
}
