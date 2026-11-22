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

@Component("google")
@RequiredArgsConstructor
public class GoogleOAuth2Client implements OAuth2Client {

    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(GoogleOAuth2Client.class);

    // user-info-uri를 제외한 나머지 필드들은 getAccessToken 메소드에서만 사용했으므로 모두 삭제.
    @Value("${spring.security.oauth2.client.provider.google.user-info-uri:}")
    private String userInfoUri;

    @Override
    public String getProvider() {
        return "google";
    }

    // --- getAccessToken(String authorizationCode) 메소드 전체 삭제 ---

    @Override
    public Mono<OAuth2UserInfo> getUserInfoByToken(String accessToken) {
        log.info("[GoogleOAuth2Client] Requesting user info with access token");
        return webClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        r -> r.bodyToMono(String.class)
                                .flatMap(msg -> {
                                    log.error("[GoogleOAuth2Client] user info error: {}", msg);
                                    return Mono.error(new RuntimeException("Google user info error: " + msg));
                                })
                )
                .bodyToMono(JsonNode.class)
                .map(userInfo -> {
                    String providerId = Objects.requireNonNull(userInfo).get("sub").asText();
                    String email = userInfo.get("email").asText();
                    String name = userInfo.get("name").asText();

                    return OAuth2UserInfo.builder()
                            .provider("google")
                            .providerId(providerId)
                            .email(email)
                            .name(name)
                            .build();
                });
    }
}