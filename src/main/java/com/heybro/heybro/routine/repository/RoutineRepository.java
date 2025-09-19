package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.user.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByRoutineTemplateTypeAndIdNotIn(UserType userType, List<Long> ids);

    List<Routine> findByRoutineTemplateType(UserType userType);

    List<Routine> findByRoutineTemplateTypeAndLevelLessThanEqual(UserType userType, int level);

    List<Routine> findByRoutineTemplateTypeAndLevelLessThanEqualAndIdNotIn(UserType userType, int level, List<Long> ids);

    List<Routine> findByLevelAndRoutineTemplateType(int level, UserType userType);

    List<Routine> findByIsCommonTrue();
}
