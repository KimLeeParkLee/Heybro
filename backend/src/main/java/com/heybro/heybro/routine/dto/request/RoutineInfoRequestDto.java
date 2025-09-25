package com.heybro.heybro.routine.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "루틴 정보 목록 요청 DTO")
public class RoutineInfoRequestDto {
    @Schema(description = "회원 루틴 수행 시간")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime scheduleTime;

    @Enumerated(EnumType.STRING)
    @Schema(description = "요일")
    private DayOfWeek dayOfWeek;
}