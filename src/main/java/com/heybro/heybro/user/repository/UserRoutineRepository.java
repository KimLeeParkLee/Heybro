package com.heybro.heybro.user.repository;

import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoutineRepository extends JpaRepository<UserRoutine, Long> {
    List<UserRoutine> findAllByUser(User user);

    Optional<UserRoutine> findByUserAndRoutine(User user, Routine routine);

    boolean existsByUserAndRoutine(User user, Routine routine);

    void deleteAllByUser(User user);
}
