package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.RoutineElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineElementRepository extends JpaRepository<RoutineElement, Long> {
}
