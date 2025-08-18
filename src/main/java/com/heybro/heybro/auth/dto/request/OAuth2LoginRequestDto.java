package com.heybro.heybro.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 로그인 요청 DTO")
public class OAuth2LoginRequestDto {
    @Schema(description = "provider")
    private String provider;

    @Schema(description = "oauth token")
    private String oauthToken;
}
