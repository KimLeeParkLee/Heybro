package com.heybro.heybro.routine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heybro.heybro.routine.domain.PeriodType;
import com.heybro.heybro.routine.domain.ViewType;
import com.heybro.heybro.routine.dto.request.RoutineAddRequestDto;
import com.heybro.heybro.routine.dto.request.RoutineModifyRequestDto;
import com.heybro.heybro.routine.dto.response.RoutineDetailResponseDto;
import com.heybro.heybro.routine.service.RoutineService;
import com.heybro.heybro.user.dto.response.RoutineAddResponseDto;
import com.heybro.heybro.user.dto.response.UserRoutineResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "특정 날짜 회원의 루틴 목록 조회")
    @GetMapping("/routine-logs")
    public UserRoutineResponseDto getRoutines(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @AuthenticationPrincipal UserDetails userDetails) {
        return routineService.getRoutinesByDate(date, userDetails.getUsername());
    }

    @Operation(summary = "루틴 상세 조회")
    @GetMapping("/{routine_id}")
    public RoutineDetailResponseDto getRoutines(@PathVariable Long routine_id) {
        return routineService.getRoutines(routine_id);
    }

    @Operation(summary = "루틴 완료")
    @PatchMapping("/{routine_id}/complete")
    public void completeUserRoutine(@PathVariable Long routine_id, @AuthenticationPrincipal UserDetails userDetails) {
        routineService.completeUserRoutine(userDetails.getUsername(), routine_id);
    }

    @Operation(summary = "루틴 달성률 조회")
    @GetMapping("/achievements")
    public Object getAchievements(@RequestParam ViewType view, @RequestParam PeriodType period, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @AuthenticationPrincipal UserDetails userDetails) {
        if (view == ViewType.list) return routineService.getListAchievements(view, period, date, userDetails.getUsername());
        else return routineService.getSummaryAchievements(view, period, date, userDetails.getUsername());
    }

    @Operation(summary = "루틴 추가")
    @PostMapping("/{routine_id}")
    public void addRoutine(@PathVariable Long routine_id, @RequestBody RoutineAddRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        routineService.addRoutine(routine_id, requestDto, userDetails.getUsername());
    }

    @Operation(summary = "추가 가능한 루틴 목록 조회")
    @GetMapping("/available")
    public RoutineAddResponseDto getAvailableRoutines(@AuthenticationPrincipal UserDetails userDetails) {
        return routineService.getAvailableRoutines(userDetails.getUsername());
    }

    @Operation(summary = "루틴 수정")
    @PutMapping("/{routine_id}")
    public void modifyRoutine(@PathVariable Long routine_id, @RequestBody RoutineModifyRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info(">>>>>> Received routine_id: {}", routine_id);
            log.info(">>>>>> Received DTO Content: {}", objectMapper.writeValueAsString(requestDto));
        } catch (Exception e) {
            log.error("DTO logging failed", e);
        }
        routineService.modifyRoutine(routine_id, requestDto, userDetails.getUsername());
    }

    @Operation(summary = "루틴 삭제")
    @DeleteMapping("/{routine_id}")
    public void deleteRoutine(@PathVariable Long routine_id, @AuthenticationPrincipal UserDetails userDetails) {
        routineService.deleteRoutine(routine_id, userDetails.getUsername());
    }
}