package com.heybro.heybro.user.service;

import com.heybro.heybro.auth.dto.response.AccessTokenResponseDto;
import com.heybro.heybro.user.dto.response.UserTypeResponseDto;
import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.EmailValidationResponseDto;
import com.heybro.heybro.user.dto.response.LoginResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    LoginResponseDto registerNewUser(UserRegistrationRequestDto requestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response);

    void logout(String accessToken);

    EmailValidationResponseDto checkEmail(String email);

    AccessTokenResponseDto reissueAccessToken(String refreshToken);

    UserTypeResponseDto getUserType(String email);
}
