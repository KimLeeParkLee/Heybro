package com.heybro.heybro.routine.domain;

import com.heybro.heybro.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "UNIQUE_LOG_USER_ROUTINE_DATE",
                columnNames = {"user_id", "routine_id", "taskDate"}
        )
})
public class DailyRoutineLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일일 루틴 기록 식별키

    private LocalDate taskDate; // 날짜

    private LocalTime scheduledTime; // 시간

    private boolean isCompleted; // 완료 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine; // 루틴

    public void toggleCompletion() {
        this.isCompleted = !this.isCompleted;
    }
}
