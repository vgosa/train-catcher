package org.group21.booking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.group21.booking.model.Booking;
import org.group21.booking.model.Route;
import org.group21.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Optional<Booking> getBookingById(long id) {
        return bookingRepository.findById(id);
    }

    public Booking addBooking(Long userId, Route route) {
        Booking booking = new Booking(userId, route);
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(long bookingId, Booking booking) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new EntityNotFoundException("Could not find a booking with ID: " + bookingId);
        }
        booking.setId(bookingId);
        return bookingRepository.save(booking);
    }

    public void deleteBooking(long id) {
        bookingRepository.deleteById(id);
    }
}
