package org.group21.ticket.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.group21.ticket.model.Ticket;
import org.group21.ticket.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.MissingResourceException;
import java.util.Optional;

@RestController
@RequestMapping("/ticket")
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> addTicket(@RequestBody @Valid Ticket ticket) {
        Ticket newTicket = ticketService.addTicket(ticket.getUserId(), ticket.getBookingId(), ticket.getIsValid());
        return ResponseEntity.status(HttpStatus.CREATED).body(newTicket);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable("ticketId") @Min(0) long ticketId) {
        Optional<Ticket> ticketOpt = ticketService.getTicketById(ticketId);
        return ticketOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable("ticketId") @Min(0) long ticketId,
                                               @RequestBody @Valid Ticket ticket) {
        try {
            Ticket updatedTicket = ticketService.updateTicket(ticketId, ticket);
            return ResponseEntity.ok(updatedTicket);
        } catch (MissingResourceException e) {
            log.error(e.getMessage(), e);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Ticket> deleteTicket(@PathVariable("ticketId") @Min(0) long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
