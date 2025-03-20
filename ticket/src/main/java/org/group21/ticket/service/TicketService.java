package org.group21.ticket.service;

import org.group21.ticket.model.Ticket;
import org.group21.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.MissingResourceException;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Optional<Ticket> getTicketById(long id) {
        return ticketRepository.findById(id);
    }

    public Ticket addTicket(long userId, long bookingId, boolean isValid) {
        Ticket ticket = new Ticket(userId, bookingId, isValid);
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(long ticketId, Ticket ticket) throws MissingResourceException{
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            throw new MissingResourceException("Could not find a ticket with ID: " + ticketId,
                    Ticket.RESOURCE_NAME, String.valueOf(ticketId));
        }
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(long ticketId) {
        ticketRepository.deleteById(ticketId);
    }
}
