package org.group21.notification.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.group21.notification.model.*;
import org.group21.notification.service.EmailService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TicketListener {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public TicketListener(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${queue.ticket}")
    public void processTicketNotification(String message) {
        try {
            Ticket ticket = objectMapper.readValue(message, Ticket.class);
            String subject = "Your Ticket Confirmation";
            String body = createTicketEmailBody(ticket);
            emailService.sendEmail(ticket.getEmail(), subject, body);
        } catch (MailException e) {
            log.error("Error processing ticket notification. CAUSE: Could not send the email confirmation!", e);
        } catch (JsonProcessingException e) {
            log.error("Error processing ticket notification. CAUSE: Could not read the dequeued message! The contents are: \n {}", message);
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error processing ticket notification.", e);
        }
    }
    private String createTicketEmailBody(Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append(ticket.getPassenger()).append(",\n\n");
        sb.append("Your ticket has been confirmed with the following journey details:\n");
        ticket.getJourneys().forEach(journey -> {
            sb.append("From ").append(journey.getDepartureStation())
                    .append(" to ").append(journey.getArrivalStation())
                    .append(" with Operator ").append(journey.getOperator().getName())
                    .append(" departing at ").append(journey.getDepartureTime())
                    .append(", travel time: ").append(journey.getTravelTime()).append(" minutes, ")
                    .append("price: ").append(journey.getPrice()).append("\n");
        });
        sb.append("\nTotal Price: ").append(ticket.getTotalPrice())
                .append("\nTotal Duration: ").append(ticket.getTotalDuration()).append(" minutes\n\n");
        sb.append("Thank you for booking with us!");
        return sb.toString();
    }

}