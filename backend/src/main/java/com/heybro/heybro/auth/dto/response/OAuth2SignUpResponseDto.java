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
@Schema(description = "OAuth2 회원가입 응답 DTO")
public class OAuth2SignUpResponseDto {
    @Schema(description = "이메일")
    private String email;

    @Schema(description = "provider")
    private String provider;
}
