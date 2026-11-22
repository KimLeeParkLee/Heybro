package com.heybro.heybro.qna.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "답변 요청 DTO")
public class AnswerRequestDto {
    @Schema(description = "내용")
    private String content;
}
