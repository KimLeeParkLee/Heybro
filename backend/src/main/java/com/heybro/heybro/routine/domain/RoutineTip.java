package com.heybro.heybro.routine.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RoutineTip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 루틴 팁 식별키

    private String content; // 루틴 팁 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine; // 루틴 요소

    public void updateRoutine(Routine routine) { this.routine = routine; }
}
