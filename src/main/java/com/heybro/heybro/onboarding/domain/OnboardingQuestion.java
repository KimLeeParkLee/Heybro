package com.heybro.heybro.onboarding.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private int displayOrder; // 정렬 순서

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnboardingOption> options = new ArrayList<>(); // 온보딩 질문 선택지 리스트

    public void addChoice(OnboardingOption option) {
        this.options.add(option);
        option.setOnboardingQuestion(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnboardingQuestion that = (OnboardingQuestion) o;
        // 데이터베이스의 PK인 'id'가 같다면 같은 객체로 인식하도록 설정
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // equals에서 사용하는 'id'로 해시코드를 생성
        return Objects.hash(id);
    }
}
