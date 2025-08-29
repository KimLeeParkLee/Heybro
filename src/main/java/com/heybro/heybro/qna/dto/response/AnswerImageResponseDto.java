package com.heybro.heybro.qna.dto.response;

import com.heybro.heybro.qna.domain.AnswerImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "답변 이미지 응답 DTO")
public class AnswerImageResponseDto {
    @Schema(description = "답변 이미지 식별키")
    private Long answerImageId;

    @Schema(description = "답변 이미지")
    private String answerImage;

    @Schema(description = "정렬 순서")
    private int sortOrder;

    public static AnswerImageResponseDto from(AnswerImage answerImage) {
        return AnswerImageResponseDto.builder()
                .answerImageId(answerImage.getId())
                .answerImage(answerImage.getAnswerImage())
                .sortOrder(answerImage.getSortOrder())
                .build();
    }
}
