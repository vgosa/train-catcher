package org.group21.trainoperator.repository;

import org.group21.trainoperator.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainRepository extends JpaRepository<Train, Integer> {
}
