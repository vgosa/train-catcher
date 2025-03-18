package org.group21.booking.repository;

import org.group21.booking.model.Booking;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BookingRepository extends CrudRepository<Booking, Long> {
}
