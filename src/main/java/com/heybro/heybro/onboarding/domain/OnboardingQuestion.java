package com.heybro.heybro.onboarding.domain;

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
public class OnboardingQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 온보딩 질문 식별키

    private String content; // 질문 내용

    private int order; // 정렬 순서

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnboardingOption> options = new ArrayList<>(); // 온보딩 질문 선택지 리스트

    public void addChoice(OnboardingOption option) {
        this.options.add(option);
        option.setOnboardingQuestion(this);
    }
}
