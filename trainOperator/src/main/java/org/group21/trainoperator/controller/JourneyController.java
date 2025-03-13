package org.group21.trainoperator.controller;

import jakarta.persistence.*;
import org.group21.trainoperator.model.*;
import org.group21.trainoperator.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/journey")
public class JourneyController {

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping
    public ResponseEntity<List<Journey>> findJourneys(
            @RequestParam(value = "departure_station", required = false) String departureStation,
            @RequestParam(value = "arrival_station", required = false) String arrivalStation,
            @RequestParam(value = "departure_time", required = false) String departureTime,
            @RequestParam(value = "isDelayed", required = false) Boolean isDelayed) {

        // For now, we ignore isDelayed as it's not implemented.
        List<Journey> journeys = journeyService.getFilteredJourneys(departureStation, arrivalStation, departureTime);
        return ResponseEntity.ok(journeys);
    }

    @PostMapping
    public ResponseEntity<Journey> addJourney(@RequestBody Journey journey) {
        Journey createdJourney = journeyService.addJourney(journey);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJourney);
    }

    @GetMapping("/{journeyId}")
    public ResponseEntity<Journey> getJourneyById(@PathVariable("journeyId") Integer journeyId) {
        Optional<Journey> journeyOpt = journeyService.getJourneyById(journeyId);
        return journeyOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{journeyId}")
    public ResponseEntity<Journey> updateJourney(@PathVariable("journeyId") Integer journeyId,
                                                 @RequestBody Journey journey) {
        Journey updatedJourney = journeyService.updateJourney(journeyId, journey);
        return ResponseEntity.ok(updatedJourney);
    }

    @DeleteMapping("/{journeyId}")
    public ResponseEntity<Void> deleteJourney(@PathVariable("journeyId") Integer journeyId) {
        journeyService.deleteJourney(journeyId);
        return ResponseEntity.noContent().build();
    }
}