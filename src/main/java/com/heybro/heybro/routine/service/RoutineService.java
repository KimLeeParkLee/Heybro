package com.heybro.heybro.routine.service;

import com.heybro.heybro.routine.dto.response.RoutineElementDetailResponseDto;
import com.heybro.heybro.user.dto.response.UserRoutineResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface RoutineService {
    UserRoutineResponseDto getRoutinesByDate(LocalDate date, String email);

    RoutineElementDetailResponseDto getRoutineElements(Long routineElementId);
}
