package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.user.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByRoutineTemplateTypeAndIdNotIn(UserType userType, List<Long> ids);

    List<Routine> findByRoutineTemplateType(UserType userType);

    List<Routine> findByRoutineTemplateTypeAndLevelLessThanEqual(UserType userType, int level);

    List<Routine> findByRoutineTemplateTypeAndLevelLessThanEqualAndIdNotIn(UserType userType, int level, List<Long> ids);

    List<Routine> findByLevelAndRoutineTemplateType(int level, UserType userType);

    List<Routine> findByIsCommonTrue();

    @Query("select r from Routine r " +
            "left join fetch r.elementList " +
            "left join fetch r.tipList " +
            "left join fetch r.recommendedProductList " +
            "where r.id = :id")
    Optional<Routine> findByIdWithDetails(@Param("id") Long id);
}
