package org.group21.trainsearch.camunda.activities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.group21.trainsearch.camunda.TicketOrderWorkflow;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
@Slf4j
public class PaymentIssuePayment implements JavaDelegate {

    private final static String PAYMENT_SERVICE_URL = "http://payment/payment";

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public PaymentIssuePayment(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        String paymentMethod = (String) execution.getVariableTyped(TicketOrderWorkflow.VARIABLE_PAYMENT_METHOD).getValue();
        Long userId = (Long) execution.getVariableTyped(TicketOrderWorkflow.VARIABLE_USER_ID).getValue();
        Long bookingId = (Long) execution.getVariableTyped(TicketOrderWorkflow.VARIABLE_BOOKING_ID).getValue();

        if (paymentMethod == null || userId == null || bookingId == null) {
            String errorMsg = "Payment method, User ID or Booking ID is null. Cannot issue payment!";
            log.error(errorMsg);
            execution.setVariable(TicketOrderWorkflow.FAILURE_REASON, errorMsg);
            throw new BpmnError(TicketOrderWorkflow.DO_NOT_RETRY, errorMsg);
        }

        log.info("Contacting the payment service to issue payment.");

        JsonNode requestBody = objectMapper.createObjectNode()
                .put("userId", userId)
                .put("bookingId", bookingId)
                .put("payment_method", paymentMethod);
        URI paymentURL = UriBuilder.fromPath(PAYMENT_SERVICE_URL + "/" + paymentMethod)
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
    }
}
