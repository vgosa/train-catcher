package org.group21.notification.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import jakarta.jms.JMSRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.group21.notification.model.*;
import org.group21.notification.service.EmailService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JourneyNotificationListener {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public JourneyNotificationListener(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${queue.journey}")
    public void receiveJourneyNotification(String message) {
        try {
            Notification notification = objectMapper.readValue(message, Notification.class);
            String subject = "Journey Notification for " + notification.getPassenger();
            String body = createNotificationEmailBody(notification);
            emailService.sendEmail(notification.getEmail(), subject, body);
        } catch (MailException e) {
            log.error("Error processing journey notification. CAUSE: Could not send the email confirmation!");
        } catch (JsonProcessingException e) {
            log.error("Error processing journey notification. CAUSE: Could not read the dequeued message!");
        } catch (Exception e) {
            log.error("Error processing journey notification.", e);
        }
    }

    private String createNotificationEmailBody(Notification notification) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append(notification.getPassenger()).append(",\n\n");
        sb.append("There is an update for your journey:\n");

        Journey journey = notification.getJourney();
        sb.append("From: ").append(journey.getDepartureStation()).append("\n");
        sb.append("To: ").append(journey.getArrivalStation()).append("\n");
        sb.append("Departure Time: ").append(journey.getDepartureTime()).append("\n");
        sb.append("Travel Time: ").append(journey.getTravelTime()).append(" minutes\n");
        sb.append("Price: ").append(journey.getPrice()).append("\n");
        if(journey.getOperator() != null) {
            sb.append("Operated by: ").append(journey.getOperator().getName());
        }
        sb.append("\nNotification Message: ").append(notification.getMessage()).append("\n\n");
        sb.append("Thank you for choosing our service!");
        return sb.toString();
    }
}