package com.heybro.heybro.qna.domain;

import com.heybro.heybro.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 질문 식별키

    private String title; // 제목

    private String content; // 내용

    private LocalDateTime createdAt; // 작성일시

    private int viewCount; // 조회수

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>(); // 해시태그

    private String thumbnail; // 대표 사진

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 회원

    @ElementCollection(targetClass = QuestionCategory.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "question_category", joinColumns = @JoinColumn(name = "question_id"))
    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 저장 (매우 중요!)
    @Column(name = "category_name") // 생성될 테이블의 컬럼명
    @Builder.Default
    private List<QuestionCategory> categories = new ArrayList<>(); // 질문 카테고리 목록

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionImage> questionImages = new ArrayList<>(); // 질문 이미지 목록

    public void addQuestionImage(QuestionImage questionImage) {
        this.questionImages.add(questionImage);
        questionImage.updateQuestion(this);
    }

    public void updateThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.updateQuestion(this); // Tag 객체의 question 필드 설정
    }
}
