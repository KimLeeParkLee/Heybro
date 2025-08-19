    package com.heybro.heybro.auth.client;

    import com.fasterxml.jackson.databind.JsonNode;
    import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
    import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
    import com.google.api.client.http.javanet.NetHttpTransport;
    import com.google.api.client.json.gson.GsonFactory;
    import com.heybro.heybro.auth.dto.OAuth2UserInfo;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.MediaType;
    import org.springframework.stereotype.Component;
    import org.springframework.util.LinkedMultiValueMap;
    import org.springframework.util.MultiValueMap;
    import org.springframework.web.reactive.function.client.WebClient;

    import java.io.IOException;
    import java.security.GeneralSecurityException;
    import java.util.Collections;
    import java.util.Objects;

    @Component("google")
    @RequiredArgsConstructor
    public class GoogleOAuth2Client implements OAuth2Client {

        private final WebClient webClient;

        @Value("${spring.security.oauth2.client.registration.google.client-id:}")
        private String clientId;

        @Value("${spring.security.oauth2.client.registration.google.client-secret:}")
        private String clientSecret;

        @Value("${spring.security.oauth2.client.registration.google.redirect-uri:}")
        private String redirectUri;

        @Value("${spring.security.oauth2.client.provider.google.token-uri:}")
        private String tokenUri;

        @Value("${spring.security.oauth2.client.provider.google.user-info-uri:}")
        private String userInfoUri;

        @Override
        public String getAccessToken(String authorizationCode) {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("redirect_uri", redirectUri);
            body.add("code", authorizationCode);

            JsonNode response = webClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            return Objects.requireNonNull(response).get("access_token").asText();
        }

        @Override
        public OAuth2UserInfo getUserInfoByToken(String idTokenString) {
            // Google ID 토큰 검증을 위한 Verifier 빌드
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken;
            try {
                // ID 토큰 검증
                idToken = verifier.verify(idTokenString);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException("Google ID Token 검증에 실패했습니다.", e);
            }

            if (idToken == null) {
                throw new IllegalArgumentException("유효하지 않은 Google ID Token 입니다.");
            }

            // 검증된 토큰에서 사용자 정보(Payload) 추출
            GoogleIdToken.Payload payload = idToken.getPayload();

            String providerId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            return OAuth2UserInfo.builder()
                    .provider("google")
                    .providerId(providerId)
                    .email(email)
                    .name(name)
                    .build();
        }
    }
