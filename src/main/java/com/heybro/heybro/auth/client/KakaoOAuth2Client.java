package com.heybro.heybro.auth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.heybro.heybro.auth.dto.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2Client implements OAuth2Client {

    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id:}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret:}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri:}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri:}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri:}")
    private String userInfoUri;

    @Override
    public OAuth2UserInfo getUserInfoByToken(String accessToken) {
        JsonNode userInfo = webClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String providerId = Objects.requireNonNull(userInfo).get("id").asText();
        String email = userInfo.get("kakao_account").get("email").asText();
        String name = userInfo.get("properties").get("nickname").asText();

        return OAuth2UserInfo.builder()
                .provider("kakao")
                .providerId(providerId)
                .email(email)
                .name(name)
                .build();
    }
}
