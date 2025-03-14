package org.group21.trainoperator.service;

import org.group21.trainoperator.model.Journey;
import org.group21.trainoperator.repository.JourneyRepository;
import org.group21.trainoperator.specification.*;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class JourneyService {

    private final JourneyRepository journeyRepository;

    public JourneyService(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }

    public List<Journey> getFilteredJourneys(String departureStation, String arrivalStation, LocalDateTime departureTime) {
        JourneyFilter filter = new JourneyFilter(departureStation, arrivalStation, departureTime);
        return journeyRepository.findAll(JourneySpecification.filterBy(filter));
    }

    // Optionally, you can keep the original method for returning all journeys
    public List<Journey> getAllJourneys() {
        return journeyRepository.findAll();
    }

    public Optional<Journey> getJourneyById(Long id) {
        return journeyRepository.findById(id);
    }

    public Journey addJourney(Journey journey) {
        journey.setId(null);
        return journeyRepository.save(journey);
    }

    public Journey updateJourney(Long id, Journey journey) {
        return journeyRepository.save(journey);
    }

    public void deleteJourney(Long id) {
        journeyRepository.deleteById(id);
    }
}
