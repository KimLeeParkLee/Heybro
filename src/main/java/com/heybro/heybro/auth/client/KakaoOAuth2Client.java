package com.heybro.heybro.auth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.heybro.heybro.auth.dto.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component("kakao")
@RequiredArgsConstructor
public class KakaoOAuth2Client implements OAuth2Client {

    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(KakaoOAuth2Client.class);

    @Value("${spring.security.oauth2.client.registration.kakao.client-id:}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri:}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri:}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri:}")
    private String userInfoUri;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public Mono<String> getAccessToken(String authorizationCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", authorizationCode);

        log.info("[KakaoOAuth2Client] Requesting access token with parameters: {}", body);

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        r -> r.bodyToMono(String.class)
                                .flatMap(msg -> {
                                    log.error("[KakaoOAuth2Client] /token error: {}", msg);
                                    return Mono.error(new RuntimeException("Kakao /token error: " + msg));
                                })
                )
                .bodyToMono(JsonNode.class)
                .map(response -> Objects.requireNonNull(response).get("access_token").asText());
    }

    @Override
    public Mono<OAuth2UserInfo> getUserInfoByToken(String accessToken) {
        return webClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(userInfo -> {
                    String providerId = Objects.requireNonNull(userInfo).get("id").asText();
                    String email = userInfo.get("kakao_account").get("email").asText();
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
