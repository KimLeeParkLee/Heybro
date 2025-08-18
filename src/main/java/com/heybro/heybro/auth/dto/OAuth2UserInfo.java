package com.heybro.heybro.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuth2UserInfo {
    @Schema(description = "provider")
    private final String provider;

    @Schema(description = "provider id")
    private final String providerId;

    @Schema(description = "email")
    private final String email;

    @Schema(description = "name")
    private final String name;

    @Builder
    public OAuth2UserInfo(String provider, String providerId, String email, String name) {
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.name = name;
    }
}
