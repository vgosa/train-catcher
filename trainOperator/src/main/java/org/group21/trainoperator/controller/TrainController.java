package org.group21.trainoperator.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.group21.trainoperator.model.Train;
import org.group21.trainoperator.service.TrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


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
    public ResponseEntity<Train> addTrain(@Valid @RequestBody Train train) {
        Train createdTrain = trainService.addTrain(train);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTrain);
    }

    @GetMapping("/{trainId}")
    public ResponseEntity<Train> getTrainById(@PathVariable("trainId") @Min(0) Long trainId) {
        Optional<Train> trainOpt = trainService.getTrainById(trainId);
        return trainOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{trainId}")
    public ResponseEntity<Train> updateTrain(@PathVariable("trainId") @Min(0) Long trainId, @Valid @RequestBody Train train) {
        Train updatedTrain = trainService.updateTrain(trainId, train);
        return ResponseEntity.ok(updatedTrain);
    }

    @DeleteMapping("/{trainId}")
    public ResponseEntity<Void> deleteTrain(@PathVariable("trainId") @Min(0) Long trainId) {
        trainService.deleteTrain(trainId);
        return ResponseEntity.noContent().build();
    }
}