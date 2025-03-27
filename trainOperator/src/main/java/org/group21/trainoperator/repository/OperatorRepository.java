package org.group21.trainoperator.repository;

import org.group21.trainoperator.model.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorRepository extends JpaRepository<Operator, Long> {
}
