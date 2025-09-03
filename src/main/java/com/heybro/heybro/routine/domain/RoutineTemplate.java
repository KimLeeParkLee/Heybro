package com.heybro.heybro.routine.domain;

import com.heybro.heybro.user.domain.UserType;
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
public class RoutineTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 루틴 템플릿 식별키

    @Enumerated(EnumType.STRING)
    private UserType type; // 루틴 유형

    @OneToMany(mappedBy = "routineTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Routine> elementList = new ArrayList<>(); // 루틴 리스트

    public void updateElementList(List<Routine> elementList) { this.elementList = elementList; }
}