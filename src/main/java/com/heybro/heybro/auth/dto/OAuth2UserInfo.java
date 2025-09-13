package com.heybro.heybro.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "OAuth2 회원 정보 DTO")
public class OAuth2UserInfo {
    @Schema(description = "provider")
    private final String provider;

    @Schema(description = "provider id")
    private final String providerId;

    @Schema(description = "email")
    private final String email;

    @Schema(description = "name")
    private final String name;
}
