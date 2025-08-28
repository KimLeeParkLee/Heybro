package com.heybro.heybro.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
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
}
