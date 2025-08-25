package com.heybro.heybro.routine.domain;

import com.heybro.heybro.user.domain.UserType;
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
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 루틴 식별키

    @Enumerated(EnumType.STRING)
    private UserType type; // 루틴 유형

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineElement> elementList = new ArrayList<>(); // 루틴 요소 리스트
}