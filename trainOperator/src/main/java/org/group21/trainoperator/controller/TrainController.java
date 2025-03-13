package org.group21.trainoperator.controller;

import org.group21.trainoperator.model.*;
import org.group21.trainoperator.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/train")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping
    public ResponseEntity<List<Train>> getTrains() {
        List<Train> trains = trainService.getAllTrains();
        return ResponseEntity.ok(trains);
    }

    @PostMapping
    public ResponseEntity<Train> addTrain(@RequestBody Train train) {
        Train createdTrain = trainService.addTrain(train);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTrain);
    }

    @GetMapping("/{trainId}")
    public ResponseEntity<Train> getTrainById(@PathVariable("trainId") Integer trainId) {
        Optional<Train> trainOpt = trainService.getTrainById(trainId);
        return trainOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{trainId}")
    public ResponseEntity<Train> updateTrain(@PathVariable("trainId") Integer trainId,
                                             @RequestBody Train train) {
        Train updatedTrain = trainService.updateTrain(trainId, train);
        return ResponseEntity.ok(updatedTrain);
    }

    @DeleteMapping("/{trainId}")
    public ResponseEntity<Void> deleteTrain(@PathVariable("trainId") Integer trainId) {
        trainService.deleteTrain(trainId);
        return ResponseEntity.noContent().build();
    }
}