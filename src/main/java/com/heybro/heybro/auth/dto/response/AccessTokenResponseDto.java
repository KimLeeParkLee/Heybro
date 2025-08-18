package com.heybro.heybro.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "access token 응답 DTO")
public class AccessTokenResponseDto {
    @Schema(description = "access token")
    private String accessToken;
}
