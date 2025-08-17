package com.heybro.heybro.user.service;

import com.heybro.heybro.common.exception.ResourceNotFoundException;
import com.heybro.heybro.common.jwt.JwtUtil;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.EmailValidationResponseDto;
import com.heybro.heybro.user.dto.response.LoginResponseDto;
import com.heybro.heybro.user.dto.response.UserRegistrationResponseDto;
import com.heybro.heybro.user.repository.UserRepository;
import com.heybro.heybro.user.security.UserDetailsImpl;
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

    @Value("${jwt.expiration.refresh-token}")
    private long refreshTokenExpiration;


    @Override
    public UserRegistrationResponseDto registerNewUser(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .userName(requestDto.getUserName())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .gender(requestDto.getGender())
                .birthDate(requestDto.getBirthDate())
                .phone(requestDto.getPhone())
                .privacyConsent(requestDto.isPrivacyConsent())
                .marketingConsent(requestDto.isMarketingConsent())
                .notificationEnabled(requestDto.isNotificationEnabled())
                .build();

        User savedUser = userRepository.save(user);

        return UserRegistrationResponseDto.builder()
                .userId(savedUser.getUserId())
                .nickname(savedUser.getNickname())
                .gender(savedUser.getGender())
                .birthDate(savedUser.getBirthDate())
                .notificationEnabled(savedUser.isNotificationEnabled())
                .broPoint(savedUser.getBroPoint())
                .broLevel(savedUser.getBroLevel())
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
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // Redis에 Refresh Token 저장
        redisTemplate.opsForValue().set(
                email,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다: " + email)
        );

        return LoginResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .notificationEnabled(user.isNotificationEnabled())
                .broPoint(user.getBroPoint())
                .broLevel(user.getBroLevel())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public EmailValidationResponseDto checkEmail(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        return EmailValidationResponseDto.builder().duplicate(exists).build();
    }
}
