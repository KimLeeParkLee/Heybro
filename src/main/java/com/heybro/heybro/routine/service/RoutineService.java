package com.heybro.heybro.routine.service;

import com.heybro.heybro.routine.domain.PeriodType;
import com.heybro.heybro.routine.domain.ViewType;
import com.heybro.heybro.routine.dto.response.AchievementListResponseDto;
import com.heybro.heybro.routine.dto.response.AchievementSummaryResponseDto;
import com.heybro.heybro.routine.dto.response.RoutineDetailResponseDto;
import com.heybro.heybro.user.dto.response.UserRoutineResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface RoutineService {
    UserRoutineResponseDto getRoutinesByDate(LocalDate date, String email);

    RoutineDetailResponseDto getRoutines(Long routineId);

    void completeUserRoutine(String email, Long routineId);

    AchievementListResponseDto getListAchievements(ViewType view, PeriodType period, LocalDate date, String email);

    AchievementSummaryResponseDto getSummaryAchievements(ViewType view, PeriodType period, LocalDate date, String email);
}
