package com.heybro.heybro.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "refresh Token 요청 DTO")
public class RefreshTokenRequestDto {
    @Schema(description = "refresh token")
    private String refreshToken;
}
