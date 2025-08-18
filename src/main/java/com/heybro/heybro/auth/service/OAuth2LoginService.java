package com.heybro.heybro.auth.service;

import com.heybro.heybro.auth.dto.request.OAuth2LoginRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface OAuth2LoginService {
    Object oauth2Login(OAuth2LoginRequestDto requestDto);
}
