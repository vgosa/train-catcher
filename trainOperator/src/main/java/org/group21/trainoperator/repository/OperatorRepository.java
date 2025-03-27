package org.group21.trainoperator.repository;

import org.group21.trainoperator.model.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OperatorRepository extends JpaRepository<Operator, Long> {

    @Query(value = "SELECT * FROM operator WHERE name = ?", nativeQuery = true)
    public Optional<Operator> findByName(String name);
}
