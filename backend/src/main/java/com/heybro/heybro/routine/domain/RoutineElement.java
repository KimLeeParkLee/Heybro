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
public class RoutineElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 루틴 요소 식별키

    private String name; // 루틴 요소 이름

    private String content; // 루틴 요소 내용

    private String detailImage; // 상세 사진

    private int step; // 단계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine; // 루틴

    public void updateRoutine(Routine routine) { this.routine = routine; }
}
