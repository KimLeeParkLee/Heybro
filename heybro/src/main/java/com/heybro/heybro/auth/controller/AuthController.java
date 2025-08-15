package com.heybro.heybro.auth.controller;

import com.heybro.heybro.common.jwt.JwtUtil;
import com.heybro.heybro.common.response.ApiResponse;
import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.expiration.refresh-token}")
    private long refreshTokenExpiration;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh-token";

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // Access Token, Refresh Token 생성
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // Redis에 Refresh Token 저장
        redisTemplate.opsForValue().set(
                email,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        // Access Token 헤더에 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);

        // Refresh Token을 HttpOnly 쿠키에 추가
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true) // HTTPS 환경에서만 전송
                .path("/")
                .maxAge(refreshTokenExpiration / 1000) // 초 단위로 변환
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ApiResponse.success("로그인에 성공했습니다.");
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
}
