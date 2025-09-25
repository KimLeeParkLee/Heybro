package com.heybro.heybro.auth.controller;

import com.heybro.heybro.auth.dto.request.OAuth2LoginRequestDto;
import com.heybro.heybro.auth.dto.request.RefreshTokenRequestDto;
import com.heybro.heybro.auth.dto.response.AccessTokenResponseDto;
import com.heybro.heybro.auth.dto.response.OAuth2SignUpResponseDto;
import com.heybro.heybro.auth.service.OAuth2LoginServiceImpl;
import com.heybro.heybro.common.jwt.JwtUtil;
import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.common.response.ApiResponse;
import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.dto.response.LoginResponseDto;
import com.heybro.heybro.user.repository.UserRepository;
import com.heybro.heybro.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "인증", description = "인증 API")
public class AuthController {
    private final UserService userService;
    private final OAuth2LoginServiceImpl oAuth2LoginServiceImpl;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    public static final String BEARER_PREFIX = "Bearer ";

    @Operation(summary = "일반 로그인")
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
    }

    @Operation(summary = "카카오 소셜 로그인 (네이티브 전용)")
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody OAuth2LoginRequestDto request) {
        Object result = oAuth2LoginServiceImpl.loginWithAccessToken("kakao", request.getAccessToken()).block();
        return createSocialLoginResponse(result);
    }

    @Operation(summary = "구글 소셜 로그인 (네이티브 전용)")
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody OAuth2LoginRequestDto request) {
        Object result = oAuth2LoginServiceImpl.loginWithAccessToken("google", request.getAccessToken()).block();
        return createSocialLoginResponse(result);
    }

    @Operation(summary = "애플 소셜 로그인 (네이티브 전용)")
    @PostMapping("/apple")
    public ResponseEntity<?> appleLogin(@RequestBody OAuth2LoginRequestDto request) {
        Object result = oAuth2LoginServiceImpl.loginWithIdentityToken("apple", request.getAccessToken());
        return createSocialLoginResponse(result);
    }

    @Operation(summary = "access token 재발행")
    @PostMapping("/refresh")
    public AccessTokenResponseDto refreshAccessToken(@RequestBody RefreshTokenRequestDto requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("Refresh Token이 없습니다.");
        }

        String refreshToken = requestDto.getRefreshToken();

        // Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        String storedRefreshToken = redisTemplate.opsForValue().get(email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 새로운 Access Token 생성
        String newAccessToken = BEARER_PREFIX + jwtUtil.createAccessToken(email);
        return AccessTokenResponseDto.builder().accessToken(newAccessToken).build();
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            authHeader = request.getHeader("authorization");
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            userService.logout(accessToken);
        }

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> createSocialLoginResponse(Object result) {
        if (result instanceof OAuth2SignUpResponseDto) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else if (result instanceof LoginResponseDto) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Unexpected response type", 500));
        }
    }
}
