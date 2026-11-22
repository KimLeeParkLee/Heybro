package com.heybro.heybro.qna.domain;

import com.heybro.heybro.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 답변 식별키

    private String content; // 내용

    private LocalDateTime createdAt; // 작성 일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question; // 질문

    // 질문 상세 조회 시 답변마다 이미지를 따로 조회하는 N+1 방지 (answer_id IN (...)으로 묶어서 조회)
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AnswerImage> answerImages = new ArrayList<>(); // 답변 이미지 목록

    public void addAnswerImage(AnswerImage answerImage) {
        this.answerImages.add(answerImage);
        answerImage.updateAnswer(this);
    }
}
