package com.heybro.heybro.auth.client;

import com.heybro.heybro.auth.dto.OAuth2UserInfo;

public interface OAuth2Client {
    String getAccessToken(String authorizationCode);
    OAuth2UserInfo getUserInfoByToken(String oauthToken);
}

