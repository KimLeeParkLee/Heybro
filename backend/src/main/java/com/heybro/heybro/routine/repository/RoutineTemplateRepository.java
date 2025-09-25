package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.RoutineTemplate;
import com.heybro.heybro.user.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineTemplateRepository extends JpaRepository<RoutineTemplate, Long> {
    RoutineTemplate findByType(UserType type);
}
