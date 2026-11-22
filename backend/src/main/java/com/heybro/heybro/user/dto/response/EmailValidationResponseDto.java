package com.heybro.heybro.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이메일 유효성 검사 응답 DTO")
public class EmailValidationResponseDto {
    @JsonProperty("is_duplicate")
    @Schema(description = "이메일 중복 여부")
    private boolean duplicate;
}
