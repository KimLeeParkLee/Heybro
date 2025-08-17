package com.heybro.heybro.user.service;

import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.EmailValidationResponseDto;
import com.heybro.heybro.user.dto.response.LoginResponseDto;
import com.heybro.heybro.user.dto.response.UserRegistrationResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserRegistrationResponseDto registerNewUser(UserRegistrationRequestDto requestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response);

    EmailValidationResponseDto checkEmail(String email);
}
