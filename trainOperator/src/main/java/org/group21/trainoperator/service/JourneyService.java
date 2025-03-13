package org.group21.trainoperator.service;

import org.group21.trainoperator.model.Journey;
import org.group21.trainoperator.repository.JourneyRepository;
import org.group21.trainoperator.specification.*;
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

    public List<Journey> getFilteredJourneys(String departureStation, String arrivalStation, String departureTimeStr) {
        LocalDateTime departureTime = null;
        if (departureTimeStr != null && !departureTimeStr.isEmpty()) {
            try {
                departureTime = LocalDateTime.parse(departureTimeStr);
            } catch (Exception e) {
                // Alternatively, handle the parse error (e.g., throw an exception or return an empty list)
                throw new IllegalArgumentException("Invalid date format for departureTime. Expected ISO_LOCAL_DATE_TIME format.");
            }
        }

        JourneyFilter filter = new JourneyFilter(departureStation, arrivalStation, departureTime);
        return journeyRepository.findAll(JourneySpecification.filterBy(filter));
    }

    // Optionally, you can keep the original method for returning all journeys
    public List<Journey> getAllJourneys() {
        return journeyRepository.findAll();
    }

    public Optional<Journey> getJourneyById(Integer id) {
        return journeyRepository.findById(id);
    }

    public Journey addJourney(Journey journey) {
        return journeyRepository.save(journey);
    }

    public Journey updateJourney(Integer id, Journey journey) {
        journey.setId(id);
        return journeyRepository.save(journey);
    }

    public void deleteJourney(Integer id) {
        journeyRepository.deleteById(id);
    }
}
