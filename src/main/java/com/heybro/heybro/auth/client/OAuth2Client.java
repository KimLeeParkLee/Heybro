package com.heybro.heybro.auth.client;

import com.heybro.heybro.auth.dto.OAuth2UserInfo;

public interface OAuth2Client {
    OAuth2UserInfo getUserInfoByToken(String oauthToken);
}

