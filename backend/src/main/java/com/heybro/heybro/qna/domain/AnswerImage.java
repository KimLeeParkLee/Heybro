package com.heybro.heybro.qna.domain;

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
public class AnswerImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 답변 이미지 식별키

    private String answerImage; // 답변 이미지

    private int sortOrder; // 정렬 순서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer; // 회원

    public void updateAnswer(Answer answer) { this.answer = answer; }
}