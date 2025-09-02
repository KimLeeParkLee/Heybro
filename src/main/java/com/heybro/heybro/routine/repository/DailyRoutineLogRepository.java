package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyRoutineLogRepository extends JpaRepository<DailyRoutineLog, Long> {
    List<DailyRoutineLog> findAllByUserAndTaskDate(User user, LocalDate taskDate);

    DailyRoutineLog findByUserAndRoutineAndTaskDate(User user, Routine routine, LocalDate taskDate);

    List<DailyRoutineLog> findAllByUserAndTaskDateBetween(User user, LocalDate startDate, LocalDate endDate);

    void deleteAllByUser(User user);
}

