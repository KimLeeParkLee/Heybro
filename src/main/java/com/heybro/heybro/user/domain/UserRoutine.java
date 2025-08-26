package com.heybro.heybro.user.domain;

import com.heybro.heybro.routine.domain.Routine;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRoutine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 루틴 일정 식별키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_element_id")
    private Routine routine; // 루틴 요소

    @OneToMany(mappedBy = "userRoutine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoutineSchedule> schedules = new ArrayList<>(); // 루틴 일정 리스트
}
