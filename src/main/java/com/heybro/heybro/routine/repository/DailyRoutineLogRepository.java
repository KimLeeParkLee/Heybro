package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyRoutineLogRepository extends JpaRepository<DailyRoutineLog, Long> {
    List<DailyRoutineLog> findAllByUserAndTaskDate(User user, LocalDate taskDate);
}
