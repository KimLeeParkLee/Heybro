package com.heybro.heybro.auth.service;

import reactor.core.publisher.Mono;

public interface OAuth2LoginService {
    Mono<Object> loginWithAccessToken(String provider, String accessToken);
    Object loginWithIdentityToken(String provider, String identityToken);
}