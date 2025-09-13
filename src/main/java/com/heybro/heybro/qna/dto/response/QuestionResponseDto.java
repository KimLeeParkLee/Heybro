package com.heybro.heybro.qna.dto.response;

import com.heybro.heybro.qna.domain.Answer;
import com.heybro.heybro.qna.domain.AnswerImage;
import com.heybro.heybro.qna.domain.Question;
import com.heybro.heybro.qna.domain.QuestionCategory;
import com.heybro.heybro.qna.dto.request.TagRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "질문 응답 DTO")
public class QuestionResponseDto {
    @Schema(description = "질문 식별키")
    private Long questionId;

    @Schema(description = "질문 제목")
    private String title;

    @Schema(description = "질문 내용")
    private String content;

    @Schema(description = "회원 식별키")
    private Long userId;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "답변 개수")
    private int answerCount;

    @Schema(description = "조회수")
    private int viewCount;

    @Schema(description = "질문 작성일시")
    private LocalDateTime createdAt;

    @Schema(description = "카테고리")
    @Builder.Default
    private List<QuestionCategory> categories = new ArrayList<>();

    @Schema(description = "태그 목록")
    @Builder.Default
    private List<TagRequestDto> tags = new ArrayList<>();

    @Schema(description = "질문 이미지 목록")
    @Builder.Default
    private List<QuestionImageResponseDto> questionImages = new ArrayList<>();

    @Schema(description = "답변 목록")
    @Builder.Default
    private List<AnswerResponseDto> answers = new ArrayList<>();

    public static QuestionResponseDto from(Question question, List<Answer> answers) {
        return QuestionResponseDto.builder()
                .questionId(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .userId(question.getUser().getId())
                .nickname(question.getUser().getNickname())
                .answerCount(answers.size())
                .viewCount(question.getViewCount())
                .createdAt(question.getCreatedAt())
                .categories(question.getCategories())
                .tags(question.getTags().stream().map(TagRequestDto::from).toList())
                .questionImages(question.getQuestionImages().stream().map(QuestionImageResponseDto::from).toList())
                .answers(answers.stream().map(AnswerResponseDto::from).toList())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "답변 응답 DTO")
    public static class AnswerResponseDto {
        @Schema(description = "답변 식별키")
        private Long answerId;

        @Schema(description = "답변 내용")
        private String content;

        @Schema(description = "회원 식별키")
        private Long userId;

        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "답변 작성일시")
        private LocalDateTime createAt;

        @Schema(description = "답변 이미지 목록")
        @Builder.Default
        private List<AnswerImageResponseDto> answerImages = new ArrayList<>();

        public static AnswerResponseDto from(Answer answer) {
            return AnswerResponseDto.builder()
                    .answerId(answer.getId())
                    .content(answer.getContent())
                    .userId(answer.getUser().getId())
                    .nickname(answer.getUser().getNickname())
                    .createAt(answer.getCreatedAt())
                    .answerImages(answer.getAnswerImages()
                            .stream()
                            .sorted(Comparator.comparing(AnswerImage::getSortOrder)) // sortOrder 순으로 오름차순 정렬
                            .map(AnswerImageResponseDto::from)
                            .toList())
                    .build();
        }
    }
}
