package org.group21.trainoperator.repository;

import jakarta.annotation.*;
import org.group21.trainoperator.model.Journey;
import org.springframework.data.jpa.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface JourneyRepository extends JpaRepository<Journey, Integer>, JpaSpecificationExecutor<Journey> {
    List<Journey> findAll(@Nullable Specification<Journey> spec);
}
