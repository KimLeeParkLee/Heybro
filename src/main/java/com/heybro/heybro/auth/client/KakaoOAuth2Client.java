package com.heybro.heybro.auth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.heybro.heybro.auth.dto.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component("kakao")
@RequiredArgsConstructor
public class KakaoOAuth2Client implements OAuth2Client {

    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(KakaoOAuth2Client.class);

    // user-info-uri만 남기고 모두 삭제
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri:}")
    private String userInfoUri;

    @Override
    public String getProvider() {
        return "kakao";
    }

    // --- getAccessToken(String authorizationCode) 메소드 전체 삭제 ---

    @Override
    public Mono<OAuth2UserInfo> getUserInfoByToken(String accessToken) {
        log.info("[KakaoOAuth2Client] Requesting user info with access token");
        return webClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        r -> r.bodyToMono(String.class)
                                .flatMap(msg -> {
                                    log.error("[KakaoOAuth2Client] /v2/user/me error: {}", msg);
                                    return Mono.error(new RuntimeException("Kakao /v2/user/me error: " + msg));
                                })
                )
                .bodyToMono(JsonNode.class)
                .map(userInfo -> {
                    String providerId = Objects.requireNonNull(userInfo).get("id").asText();
                    JsonNode kakaoAccount = userInfo.get("kakao_account");
                    String email = kakaoAccount.has("email") ? kakaoAccount.get("email").asText() : null;
                    String name = userInfo.get("properties").get("nickname").asText();

                    return OAuth2UserInfo.builder()
                            .provider("kakao")
                            .providerId(providerId)
                            .email(email)
                            .name(name)
                            .build();
                });
    }
}