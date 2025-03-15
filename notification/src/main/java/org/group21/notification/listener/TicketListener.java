package org.group21.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.group21.notification.model.*;
import org.group21.notification.service.EmailService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class TicketListener {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TicketListener(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${queue.ticket}")
    public void receiveTicketNotification(String message) {
        try {
            Ticket ticket = objectMapper.readValue(message, Ticket.class);
            String subject = "Your Ticket Confirmation";
            String body = createTicketEmailBody(ticket);
            emailService.sendEmail(ticket.getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Error processing ticket notification: " + e.getMessage());
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