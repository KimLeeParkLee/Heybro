package com.heybro.heybro.auth.client;

import com.heybro.heybro.auth.dto.OAuth2UserInfo;
import reactor.core.publisher.Mono;

public interface OAuth2Client {
    Mono<String> getAccessToken(String authorizationCode);
    Mono<OAuth2UserInfo> getUserInfoByToken(String oauthToken);
}

