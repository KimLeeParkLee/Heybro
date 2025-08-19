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
        public OAuth2UserInfo getUserInfoByToken(String accessToken) {
            JsonNode userInfo = webClient.get()
                    .uri(userInfoUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            String providerId = Objects.requireNonNull(userInfo).get("sub").asText();
            String email = userInfo.get("email").asText();
            String name = userInfo.get("name").asText();

            return OAuth2UserInfo.builder()
                    .provider("google")
                    .providerId(providerId)
                    .email(email)
                    .name(name)
                    .build();
        }
    }
