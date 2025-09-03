package com.heybro.heybro.routine.domain;

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
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 루틴 식별키

    private String name; // 루틴 이름

    @Builder.Default
    private Integer level = 1;

    @Enumerated(EnumType.STRING)
    private TimeOfDay timeOfDay; // 루틴 시간대 (MORNING, LUNCH, EVENING)

    private String iconImage; // 아이콘 url

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecommendedProduct> recommendedProductList = new ArrayList<>(); // 추천 상품 목록

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_template_id")
    private RoutineTemplate routineTemplate; // 루틴 탬플랏

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoutineElement> elementList = new ArrayList<>(); // 루틴 요소 리스트

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("id ASC")
    private List<RoutineTip> tipList = new ArrayList<>(); // 루틴 팁 리스트

    public void updateRoutineTemplate(RoutineTemplate routineTemplate) {
        this.routineTemplate = routineTemplate;
    }
}