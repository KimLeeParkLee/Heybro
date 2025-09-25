package com.heybro.heybro.qna.dto.response;

import com.heybro.heybro.qna.domain.QuestionImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "질문 이미지 응답 DTO")
public class QuestionImageResponseDto {
    @Schema(description = "질문 이미지 식별키")
    private Long questionImageId;

    @Schema(description = "질문 이미지")
    private String questionImage;

    @Schema(description = "정렬 순서")
    private int sortOrder;

    public static QuestionImageResponseDto from(QuestionImage questionImage) {
        return QuestionImageResponseDto.builder()
                .questionImageId(questionImage.getId())
                .questionImage(questionImage.getQuestionImage())
                .sortOrder(questionImage.getSortOrder())
                .build();
    }
}
