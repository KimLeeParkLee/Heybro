package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.user.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyRoutineLogRepository extends JpaRepository<DailyRoutineLog, Long> {
    @Query("select d from DailyRoutineLog d join fetch d.routine where d.user = :user and d.taskDate = :taskDate")
    List<DailyRoutineLog> findAllByUserAndTaskDateWithRoutine(@Param("user") User user, @Param("taskDate") LocalDate taskDate);

    List<DailyRoutineLog> findAllByUserAndTaskDate(User user, LocalDate taskDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    DailyRoutineLog findByUserAndRoutineAndTaskDate(User user, Routine routine, LocalDate taskDate);

    @Query("select d from DailyRoutineLog d join fetch d.routine where d.user = :user and d.taskDate between :startDate and :endDate")
    List<DailyRoutineLog> findAllByUserAndTaskDateBetweenWithRoutine(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<DailyRoutineLog> findAllByUserAndTaskDateBetween(User user, LocalDate startDate, LocalDate endDate);

    void deleteAllByUser(User user);
}

