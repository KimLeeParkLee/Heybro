package com.heybro.heybro.routine.controller;

import com.heybro.heybro.routine.dto.response.RoutineElementDetailResponseDto;
import com.heybro.heybro.routine.service.RoutineService;
import com.heybro.heybro.user.dto.response.UserRoutineResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/routines")
@Tag(name = "루틴", description = "루틴 API")
@Slf4j
public class RoutineController {
    private final RoutineService routineService;

    @Operation(summary = "특정 날짜 회원의 루틴 조회")
    @GetMapping("/routine-logs")
    public UserRoutineResponseDto getRoutines(@RequestParam LocalDate date, @AuthenticationPrincipal UserDetails userDetails) {
        return routineService.getRoutinesByDate(date, userDetails.getUsername());
    }

    @Operation(summary = "루틴 요소 상세 조회")
    @GetMapping("/{routineElementId}")
    public RoutineElementDetailResponseDto getRoutineElements(@PathVariable Long routineElementId) {
        return routineService.getRoutineElements(routineElementId);
    }
}