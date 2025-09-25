package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.RecommendedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendedProductRepository extends JpaRepository<RecommendedProduct, Long> {
    List<RecommendedProduct> findByRoutineId(Long routineId);
}
