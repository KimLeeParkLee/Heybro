package com.heybro.heybro.auth.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heybro.heybro.auth.dto.OAuth2UserInfo;
import com.heybro.heybro.auth.dto.response.ApplePublicKeyResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Component("apple")
@RequiredArgsConstructor
public class AppleOAuth2Client implements OAuth2Client {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${oauth2.apple.client-id}")
    private String clientId;

    @Value("${oauth2.apple.iss}")
    private String iss;

    @Value("${oauth2.apple.public-key-url}")
    private String publicKeyUrl;

    @Override
    public String getProvider() {
        return "apple";
    }

    @Override
    public Mono<String> getAccessToken(String authorizationCode) {
        return Mono.error(new UnsupportedOperationException("Apple login does not use authorization code."));
    }

    @Override
    public Mono<OAuth2UserInfo> getUserInfoByToken(String oauthToken) {
        return Mono.error(new UnsupportedOperationException("Apple login does not use access token to get user info."));
    }

    @Override
    public OAuth2UserInfo getUserInfoByIdentityToken(String identityToken) {
        Claims body = getClaims(identityToken);
        String appleUniqueId = body.getSubject();
        String email = body.get("email", String.class);

        return OAuth2UserInfo.builder()
                .provider(getProvider())
                .providerId(appleUniqueId)
                .email(email)
                .build();
    }

    private Claims getClaims(String identityToken) {
        try {
            String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));
            Map<String, String> header = objectMapper.readValue(new String(Base64.getDecoder().decode(headerOfIdentityToken), StandardCharsets.UTF_8), Map.class);

            ApplePublicKeyResponse keys = restTemplate.getForObject(publicKeyUrl, ApplePublicKeyResponse.class);
            ApplePublicKeyResponse.Key key = keys.getMatchedKey(header.get("kid"), header.get("alg"))
                    .orElseThrow(() -> new IllegalArgumentException("Failed to find matching key"));

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            // Correct usage for jjwt 0.12.x
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer(iss)
                    .requireAudience(clientId)
                    .build()
                    .parseSignedClaims(identityToken)
                    .getPayload();

        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to parse apple identity token", e);
        }
    }
}