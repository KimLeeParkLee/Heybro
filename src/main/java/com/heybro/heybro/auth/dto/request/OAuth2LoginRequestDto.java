package com.heybro.heybro.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 로그인 요청 DTO")
public class OAuth2LoginRequestDto {
    @Schema(description = "access token")
    @JsonProperty("access_token")
    private String accessToken;
}
