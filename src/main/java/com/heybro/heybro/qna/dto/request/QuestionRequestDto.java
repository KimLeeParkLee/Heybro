package com.heybro.heybro.qna.dto.request;

import com.heybro.heybro.qna.domain.QuestionCategory;
import com.heybro.heybro.qna.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "질문 요청 DTO")
public class QuestionRequestDto {
    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "질문 제목")
    @ElementCollection(targetClass = QuestionCategory.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "question_category", joinColumns = @JoinColumn(name = "question_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category_name")
    @Builder.Default
    private List<QuestionCategory> categories = new ArrayList<>();

    @Schema(description = "해시 태그")
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();
}
