package org.group21.trainoperator.service;

import org.group21.trainoperator.model.Train;
import org.group21.trainoperator.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainService {

    private final TrainRepository trainRepository;

    public TrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    public Optional<Train> getTrainById(Integer id) {
        return trainRepository.findById(id);
    }

    public Train addTrain(Train train) {
        return trainRepository.save(train);
    }

    public Train updateTrain(Integer id, Train train) {
        train.setId(id);
        return trainRepository.save(train);
    }

    public void deleteTrain(Integer id) {
        trainRepository.deleteById(id);
    }
}
