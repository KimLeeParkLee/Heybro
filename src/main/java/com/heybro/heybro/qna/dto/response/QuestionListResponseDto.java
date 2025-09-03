package com.heybro.heybro.qna.dto.response;

import com.heybro.heybro.qna.domain.Answer;
import com.heybro.heybro.qna.domain.Question;
import com.heybro.heybro.qna.domain.QuestionCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "질문 목록 응답 DTO")
public class QuestionListResponseDto {
    @Schema(description = "질문 식별키")
    private Long questionId;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "제목")
    private String content;

    @Schema(description = "질문 식별키")
    private LocalDateTime createdAt;

    @Schema(description = "질문 식별키")
    private int answerCount;

    @Schema(description = "질문 식별키")
    private int viewCount;

    @Schema(description = "질문 식별키")
    private String thumbnail;

    @Schema(description = "질문 식별키")
    @Builder.Default
    private List<QuestionCategory> categories = new ArrayList<>();

    public static QuestionListResponseDto from(Question question, List<Answer> answer) {
        return QuestionListResponseDto.builder()
                .questionId(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .createdAt(question.getCreatedAt())
                .answerCount(answer != null ? answer.size() : 0)
                .viewCount(question.getViewCount())
                .thumbnail(question.getThumbnail())
                .categories(question.getCategories())
                .build();
    }
}
