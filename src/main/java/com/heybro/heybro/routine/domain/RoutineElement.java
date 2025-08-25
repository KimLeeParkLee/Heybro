package com.heybro.heybro.routine.domain;

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
public class RoutineElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 루틴 요소 식별키

    private String name; // 루틴 요소 이름

    @Enumerated(EnumType.STRING)
    private TimeOfDay timeOfDay; // 루틴 시간대 (MORNING, LUNCH, EVENING)

    private String iconImage; // 아이콘 url

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine; // 루틴

    @OneToMany(mappedBy = "routineElement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineSubElement> subElementList = new ArrayList<>();

    protected void setRoutine(Routine routine) {
        this.routine = routine;
    }
}