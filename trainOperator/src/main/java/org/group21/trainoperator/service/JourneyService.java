package org.group21.trainoperator.service;

import org.group21.trainoperator.model.Journey;
import org.group21.trainoperator.repository.JourneyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JourneyService {

    private final JourneyRepository journeyRepository;

    public JourneyService(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }

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
