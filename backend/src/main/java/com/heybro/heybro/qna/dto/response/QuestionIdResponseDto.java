package com.heybro.heybro.qna.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "질문 아이디 응답 DTO")
public class QuestionIdResponseDto {
    @Schema(description = "질문 식별키")
    private Long questionId;
}
