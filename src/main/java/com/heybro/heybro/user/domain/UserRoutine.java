package com.heybro.heybro.user.domain;

import com.heybro.heybro.routine.domain.Routine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserRoutine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 루틴 일정 식별키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine; // 루틴

    @OneToMany(mappedBy = "userRoutine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserRoutineSchedule> schedules = new ArrayList<>(); // 루틴 일정 리스트

    public void addSchedule(UserRoutineSchedule schedule) {
        schedules.add(schedule);
        schedule.setUserRoutine(this);
    }

    public void updateSchedules(List<UserRoutineSchedule> newSchedules) {
        // 1. 삭제할 스케줄 찾기 (기존 스케줄에는 있지만, 새로운 스케줄에는 없는 것)
        List<UserRoutineSchedule> schedulesToRemove = this.schedules.stream()
                .filter(existingSchedule -> !newSchedules.contains(existingSchedule))
                .toList();

        // 2. 추가할 스케줄 찾기 (새로운 스케줄에는 있지만, 기존 스케줄에는 없는 것)
        List<UserRoutineSchedule> schedulesToAdd = newSchedules.stream()
                .filter(newSchedule -> !this.schedules.contains(newSchedule))
                .toList();

        // 3. 찾아낸 리스트를 바탕으로 실제 컬렉션 변경
        schedulesToRemove.forEach(schedule -> this.schedules.remove(schedule));
        schedulesToAdd.forEach(this::addSchedule); // 연관관계 설정을 위해 addSchedule 메서드 사용
    }
}
