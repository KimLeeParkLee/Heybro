package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.RoutineTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineTipRepository extends JpaRepository<RoutineTip, Integer> {
}
