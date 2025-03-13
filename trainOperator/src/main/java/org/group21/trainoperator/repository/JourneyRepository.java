package org.group21.trainoperator.repository;

import org.group21.trainoperator.model.Journey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyRepository extends JpaRepository<Journey, Integer> {
}
