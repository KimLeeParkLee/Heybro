package com.heybro.heybro.auth.controller;

import com.heybro.heybro.auth.controller.dto.request.RefreshTokenRequestDto;
import com.heybro.heybro.auth.controller.dto.response.AccessTokenResponseDto;
import com.heybro.heybro.common.jwt.JwtUtil;
import com.heybro.heybro.common.response.ApiResponse;
import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.dto.response.LoginResponseDto;
import com.heybro.heybro.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
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
    public static final String BEARER_PREFIX = "Bearer ";

    @Operation(summary = "일반 로그인")
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return ApiResponse.success(userService.login(loginRequestDto, response));
    }

    @Operation(summary = "access token 재발행")
    @PostMapping("/refresh")
    public AccessTokenResponseDto refreshAccessToken(@RequestBody RefreshTokenRequestDto requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("Refresh Token이 없습니다.");
        }

        // Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(requestDto.getRefreshToken())) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
        }

        String email = jwtUtil.getEmailFromToken(requestDto.getRefreshToken());
        String storedRefreshToken = redisTemplate.opsForValue().get(email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(requestDto.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        // 새로운 Access Token 생성
        String newAccessToken = BEARER_PREFIX + jwtUtil.generateAccessToken(email);
        return AccessTokenResponseDto.builder().accessToken(newAccessToken).build();
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            userService.logout(accessToken);
        }

        return ResponseEntity.noContent().build();
    }
}
