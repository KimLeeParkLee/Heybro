package com.heybro.heybro.routine.service;

import com.heybro.heybro.routine.domain.PeriodType;
import com.heybro.heybro.routine.domain.ViewType;
import com.heybro.heybro.routine.dto.request.RoutineAddRequestDto;
import com.heybro.heybro.routine.dto.request.RoutineModifyRequestDto;
import com.heybro.heybro.routine.dto.response.AchievementListResponseDto;
import com.heybro.heybro.routine.dto.response.AchievementSummaryResponseDto;
import com.heybro.heybro.routine.dto.response.RoutineDetailResponseDto;
import com.heybro.heybro.user.dto.response.RoutineAddResponseDto;
import com.heybro.heybro.user.dto.response.UserRoutineResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface RoutineService {
    UserRoutineResponseDto getRoutinesByDate(LocalDate date, String email);

    RoutineDetailResponseDto getRoutines(Long routineId);

    void completeUserRoutine(String email, Long routineId);

    AchievementListResponseDto getListAchievements(ViewType view, PeriodType period, LocalDate date, String email);

    AchievementSummaryResponseDto getSummaryAchievements(ViewType view, PeriodType period, LocalDate date, String email);

    void addRoutine(Long routineId, RoutineAddRequestDto requestDto, String email);

    void modifyRoutine(Long routineId, RoutineModifyRequestDto requestDto, String email);

    void deleteRoutine(Long routineId, String email);

    RoutineAddResponseDto getAvailableRoutines(String email);
}
