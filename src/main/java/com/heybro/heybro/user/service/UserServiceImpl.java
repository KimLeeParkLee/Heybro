package com.heybro.heybro.user.service;

import com.heybro.heybro.auth.dto.response.AccessTokenResponseDto;
import com.heybro.heybro.common.jwt.JwtUtil;
import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.dto.request.LoginRequestDto;
import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.*;
import com.heybro.heybro.common.exception.BusinessException;
import com.heybro.heybro.common.exception.ErrorCode;
import com.heybro.heybro.coupon.repository.CouponPurchaseRepository;
import com.heybro.heybro.onboarding.repository.UserOnboardingAnswerRepository;
import com.heybro.heybro.point.repository.PointHistoryRepository;
import com.heybro.heybro.routine.repository.DailyRoutineLogRepository;
import com.heybro.heybro.skin.repository.SkinDiagnosisRepository;
import com.heybro.heybro.user.repository.UserRepository;
import com.heybro.heybro.user.repository.UserRoutineRepository;
import com.heybro.heybro.user.repository.UserRoutineScheduleRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final DailyRoutineLogRepository dailyRoutineLogRepository;
    private final UserRoutineScheduleRepository userRoutineScheduleRepository;
    private final UserRoutineRepository userRoutineRepository;
    private final UserOnboardingAnswerRepository userOnboardingAnswerRepository;
    private final SkinDiagnosisRepository skinDiagnosisRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final CouponPurchaseRepository couponPurchaseRepository;
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.expiration.refresh-token}")
    private long refreshTokenExpiration;


    @Override
    public LoginResponseDto registerNewUser(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (requestDto.getNickname() != null && userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        String encodedPassword = null;
        if (requestDto.getPassword() != null) {
            encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        }

        User user = User.builder()
                .name(requestDto.getUserName())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .gender(requestDto.getGender())
                .birthDate(requestDto.getBirthDate())
                .phone(requestDto.getPhone())
                .privacyConsent(requestDto.isPrivacyConsent())
                .marketingConsent(requestDto.isMarketingConsent())
                .notificationEnabled(requestDto.isNotificationEnabled())
                .provider(requestDto.getProvider())
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
                .userType(user.getUserType())
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
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
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

    @Override
    public NicknameAvailableResponseDto isNicknameAvailable(String nickname) {
        return NicknameAvailableResponseDto.builder().isAvailable(userRepository.findByNickname(nickname).isEmpty()).build();
    }

    @Override
    @Transactional
    public void updateFcmToken(String email, String fcmToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        user.updateFcmToken(fcmToken);
        userRepository.save(user);
    }

    @Override
    public UserRankingResponseDto getUserRankings(String email) {
        List<com.heybro.heybro.user.repository.UserRankingProjection> top10Rankings = userRepository.findTop10UserRankingProjections();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        int myRanking = userRepository.findMyRanking(email);

        return UserRankingResponseDto.from(top10Rankings, user, myRanking);
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 이전에는 userRepository.delete 가 컬렉션 9개를 각각 로딩해 행마다 DELETE 했고,
        // 보낸 선물 쿠폰과 남의 질문에 단 답변까지 함께 지워졌다.
        // 개인 데이터는 벌크로 지우고, 회원 행은 지우지 않고 익명화한다 (질문·답변은 "탈퇴한 회원"으로 남음).
        dailyRoutineLogRepository.deleteBulkByUser(user);
        userRoutineScheduleRepository.deleteBulkByUser(user);
        userRoutineRepository.deleteBulkByUser(user);
        userOnboardingAnswerRepository.deleteBulkByUser(user);
        skinDiagnosisRepository.deleteBulkByUser(user);
        pointHistoryRepository.deleteBulkByUser(user);
        couponPurchaseRepository.deleteBulkByUser(user);
        redisTemplate.delete(user.getEmail()); // refresh token 폐기
        user.withdraw();
    }

    @Override
    @Transactional
    public void updatePassword(String password, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        user.updatePassword(passwordEncoder.encode(password));
    }
}
