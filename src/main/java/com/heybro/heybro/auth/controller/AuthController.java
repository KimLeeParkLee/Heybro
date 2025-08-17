package com.heybro.heybro.auth.controller;

import com.heybro.heybro.common.jwt.JwtUtil;
import com.heybro.heybro.common.response.ApiResponse;
import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.dto.response.LoginResponseDto;
import com.heybro.heybro.user.security.UserDetailsImpl;
import com.heybro.heybro.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh-token";

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return ApiResponse.success(userService.login(loginRequestDto, response));
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refreshAccessToken(@CookieValue(REFRESH_TOKEN_COOKIE_NAME) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh Token이 없습니다.");
        }

        // Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        String storedRefreshToken = redisTemplate.opsForValue().get(email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        // 새로운 Access Token 생성
        String newAccessToken = jwtUtil.generateAccessToken(email);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);

        return ApiResponse.success("Access Token이 재발급되었습니다.");
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.logout(userDetails.getUsername());
        return ApiResponse.success("성공적으로 로그아웃되었습니다.");
    }
}
