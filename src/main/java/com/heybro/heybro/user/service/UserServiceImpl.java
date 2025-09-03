package com.heybro.heybro.user.service;

import com.heybro.heybro.auth.dto.response.AccessTokenResponseDto;
import com.heybro.heybro.common.jwt.JwtUtil;
import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.EmailValidationResponseDto;
import com.heybro.heybro.user.dto.response.LoginResponseDto;
import com.heybro.heybro.user.dto.response.UserTypeResponseDto;
import com.heybro.heybro.user.repository.UserRepository;
import com.heybro.heybro.user.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.expiration.refresh-token}")
    private long refreshTokenExpiration;


    @Override
    public LoginResponseDto registerNewUser(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .name(requestDto.getUserName())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .gender(requestDto.getGender())
                .birthDate(requestDto.getBirthDate())
                .phone(requestDto.getPhone())
                .profileImage("https://drive.google.com/file/d/1HkRMMWYo0bWc8qWbkEDL5xCcr1GQ1rJu/view?usp=sharing")
                .privacyConsent(requestDto.isPrivacyConsent())
                .marketingConsent(requestDto.isMarketingConsent())
                .notificationEnabled(requestDto.isNotificationEnabled())
                .build();

        // 1. 사용자 정보 DB에 저장
        User savedUser = userRepository.save(user);

        // 2. Access Token, Refresh Token 생성
        String email = savedUser.getEmail();
        String accessToken = BEARER_PREFIX + jwtUtil.createAccessToken(email);
        String refreshToken = jwtUtil.createRefreshToken(email);

        // 3. Redis에 Refresh Token 저장
        redisTemplate.opsForValue().set(
                email,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        // 4. LoginResponseDto 형태로 사용자 정보와 토큰 반환
        return LoginResponseDto.builder()
                .userId(savedUser.getId())
                .nickname(savedUser.getNickname())
                .gender(savedUser.getGender())
                .birthDate(savedUser.getBirthDate())
                .profileImage(savedUser.getProfileImage())
                .notificationEnabled(savedUser.isNotificationEnabled())
                .broPoint(savedUser.getBroPoint())
                .broLevel(savedUser.getBroLevel())
                .experience(savedUser.getExperience())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // Access Token, Refresh Token 생성
        String accessToken = BEARER_PREFIX + jwtUtil.createAccessToken(email);
        String refreshToken = jwtUtil.createRefreshToken(email);

        // Redis에 Refresh Token 저장
        redisTemplate.opsForValue().set(
                email,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        return LoginResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .profileImage(user.getProfileImage())
                .notificationEnabled(user.isNotificationEnabled())
                .broPoint(user.getBroPoint())
                .broLevel(user.getBroLevel())
                .experience(user.getExperience())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(String accessToken) {
        long remainingTime = jwtUtil.getRemainingTime(accessToken);

        // 남은 유효 시간이 0보다 클 경우, Redis에 블랙리스트로 추가
        // Key: accessToken, Value: "logout", TTL: 남은 유효 시간
        if (remainingTime > 0) {
            redisTemplate.opsForValue().set(
                    accessToken,
                    "logout",
                    remainingTime,
                    TimeUnit.MILLISECONDS
            );
        }

        // refresh token 삭제
        redisTemplate.delete(jwtUtil.getEmailFromToken(accessToken));

    }

    @Override
    public EmailValidationResponseDto checkEmail(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        return EmailValidationResponseDto.builder().duplicate(exists).build();
    }

    @Override
    public AccessTokenResponseDto reissueAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        String storedRefreshToken = redisTemplate.opsForValue().get(email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String newAccessToken = BEARER_PREFIX + jwtUtil.createAccessToken(user.getEmail());

        return AccessTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    public UserTypeResponseDto getUserType(String email) {
        return UserTypeResponseDto.builder()
                .userType(userRepository.findUserTypeByEmail(email))
                .build();
    }
}
