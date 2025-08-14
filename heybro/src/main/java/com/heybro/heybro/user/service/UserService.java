package com.heybro.heybro.user.service;

import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.UserRegistrationResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserRegistrationResponseDto registerNewUser(UserRegistrationRequestDto requestDto);
}
