package org.group21.trainoperator.controller;

import jakarta.persistence.*;
import org.group21.trainoperator.model.*;
import org.group21.trainoperator.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/journey")
public class JourneyController {

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Journey>> findJourneys(
            @RequestParam(value = "departure_station", required = false) String departureStation,
            @RequestParam(value = "arrival_station", required = false) String arrivalStation,
            @RequestParam(value = "departure_time", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
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
    public ResponseEntity<Journey> getJourneyById(@PathVariable("journeyId") Long journeyId) {
        Optional<Journey> journeyOpt = journeyService.getJourneyById(journeyId);
        return journeyOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{journeyId}")
    public ResponseEntity<Journey> updateJourney(@PathVariable("journeyId") Long journeyId,
                                                 @RequestBody Journey journey) {
        Journey updatedJourney = journeyService.updateJourney(journeyId, journey);
        return ResponseEntity.ok(updatedJourney);
    }

    @DeleteMapping("/{journeyId}")
    public ResponseEntity<Void> deleteJourney(@PathVariable("journeyId") Long journeyId) {
        journeyService.deleteJourney(journeyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{journeyId}/block")
    public ResponseEntity<Void> blockSeat(@PathVariable Long journeyId) {
        boolean success = journeyService.blockSeat(journeyId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{journeyId}/confirm")
    public ResponseEntity<Void> confirmSeat(@PathVariable Long journeyId) {
        boolean success = journeyService.confirmSeat(journeyId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{journeyId}/cancel")
    public ResponseEntity<Void> cancelSeat(@PathVariable Long journeyId) {
        boolean success = journeyService.cancelSeat(journeyId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}