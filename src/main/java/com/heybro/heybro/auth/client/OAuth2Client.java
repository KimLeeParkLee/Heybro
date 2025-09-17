package com.heybro.heybro.auth.client;

import com.heybro.heybro.auth.dto.OAuth2UserInfo;
import reactor.core.publisher.Mono;

public interface OAuth2Client {
    String getProvider();

    Mono<String> getAccessToken(String authorizationCode);

    Mono<OAuth2UserInfo> getUserInfoByToken(String oauthToken);

    default OAuth2UserInfo getUserInfoByIdentityToken(String identityToken) {
        throw new UnsupportedOperationException("This provider does not support login with an identity token.");
    }
}

