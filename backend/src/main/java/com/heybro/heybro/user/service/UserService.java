package com.heybro.heybro.user.service;

import com.heybro.heybro.auth.dto.response.AccessTokenResponseDto;
import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.*;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    LoginResponseDto registerNewUser(UserRegistrationRequestDto requestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response);

    void logout(String accessToken);

    EmailValidationResponseDto checkEmail(String email);

    AccessTokenResponseDto reissueAccessToken(String refreshToken);

    UserTypeResponseDto getUserType(String email);

    NicknameAvailableResponseDto isNicknameAvailable(String nickname);

    void updateFcmToken(String email, String fcmToken);

    UserRankingResponseDto getUserRankings(String email);

    void deleteUser(String email);

    void updatePassword(String password, String email);
}
