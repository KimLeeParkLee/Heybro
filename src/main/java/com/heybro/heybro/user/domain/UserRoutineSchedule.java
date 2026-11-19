package com.heybro.heybro.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
// 매분 실행되는 알림 스케줄러가 (요일, 시각) 조건으로 조회하므로 전체 스캔을 피하기 위한 복합 인덱스
@Table(indexes = @Index(name = "idx_user_routine_schedule_day_time", columnList = "dayOfWeek, scheduleTime"))
public class UserRoutineSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 회원 루틴 일정 식별키

    private LocalTime scheduleTime; // 회원 루틴 수행 시간

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // 요일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_routine_id")
    private UserRoutine userRoutine; // 회원 루틴 요소

    public void updateScheduleTime(LocalTime newScheduleTime) { this.scheduleTime = newScheduleTime; }
}
