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
public class RoutineSubElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 루틴 서브 요소 식별키

    private String name; // 루틴 서브 요소 이름

    private String content; // 루틴 서브 요소 내용

    private String detailImage; // 상세 사진

    private int step; // 단계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_element_id")
    private RoutineElement routineElement;

    @OneToMany(mappedBy = "routineSubElement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineTip> tipList = new ArrayList<>();
}
