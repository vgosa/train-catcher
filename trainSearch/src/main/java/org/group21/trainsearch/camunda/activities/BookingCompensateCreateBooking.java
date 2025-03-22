package org.group21.trainsearch.camunda.activities;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class BookingCompensateCreateBooking implements JavaDelegate {

    private final RestTemplate restTemplate;

    public BookingCompensateCreateBooking(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));

        Long bookingId = (Long) execution.getVariable("bookingId");
        if (bookingId == null) {
            log.info("No bookingId found in the context. No compensation is required.");
            return;
        }

        log.info("Compensating booking creation for bookingId: {}", bookingId);

        try {
            restTemplate.delete("http://booking/booking/" + bookingId);
        } catch (RestClientException e) {
            log.error("Failed to compensate booking creation. CAUSE: Could not contact the booking service to delete the booking.", e);
            return;
        }

        log.info("Successfully compensated booking creation for bookingId: {}", bookingId);
    }
}
