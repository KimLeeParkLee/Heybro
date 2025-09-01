package com.heybro.heybro.user.repository;

import com.heybro.heybro.user.domain.UserRoutineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface UserRoutineScheduleRepository extends JpaRepository<UserRoutineSchedule, Long> {
    List<UserRoutineSchedule> findAllByDayOfWeekEquals(DayOfWeek today);

    List<UserRoutineSchedule> findAllByDayOfWeekEqualsAndScheduleTimeEquals(DayOfWeek dayOfWeek, LocalTime time);
}
