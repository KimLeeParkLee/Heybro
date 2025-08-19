package com.heybro.heybro.auth.service;

import com.heybro.heybro.auth.dto.request.OAuth2LoginRequestDto;
import com.heybro.heybro.auth.client.OAuth2Client;
import com.heybro.heybro.auth.dto.OAuth2UserInfo;
import com.heybro.heybro.auth.dto.response.OAuth2SignUpResponseDto;
import com.heybro.heybro.common.jwt.JwtUtil;
import com.heybro.heybro.user.domain.User;

import com.heybro.heybro.user.dto.response.LoginResponseDto;
import com.heybro.heybro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2LoginServiceImpl implements OAuth2LoginService {
    private final Map<String, OAuth2Client> oAuth2Clients;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.expiration.refresh-token}")
    private long refreshTokenExpiration;

    @Override
    public Object oauth2Login(OAuth2LoginRequestDto requestDto) {
        // Provider에 맞는 OAuth2 Client 가져오기
        OAuth2Client oAuth2Client = oAuth2Clients.get(requestDto.getProvider().toLowerCase());

        if (oAuth2Client == null) {
            throw new IllegalArgumentException("Unsupported provider: " + requestDto.getProvider());
        }

        // 1. 클라이언트에서 받은 Authorization Code로 Provider의 Access Token 발급받기
        // DTO의 oauthToken 필드가 실제로는 Authorization Code를 담고 있다고 가정합니다.
        String authorizationCode = requestDto.getOauthToken();
        String accessToken = oAuth2Client.getAccessToken(authorizationCode);

        // 2. 발급받은 Access Token으로 사용자 정보 조회
        OAuth2UserInfo userInfo = oAuth2Client.getUserInfoByToken(accessToken);

        // 3. 사용자 정보로 DB에서 유저를 찾거나 신규 생성 (upsert)
        UserUpsertResult result = upsertUser(userInfo);
        User user = result.user();

        // 4. 신규/기존 유저에 따라 분기 처리 (요청에 따라 이 로직은 유지)
        if (result.isNewUser()) {
            // 신규 가입일 경우: 추가 정보 입력을 위해 관련 정보 반환
            return OAuth2SignUpResponseDto.builder()
                    .email(user.getEmail())
                    .provider(user.getProvider())
                    .build();
        } else {
            // 기존 회원 로그인일 경우: 자체 서비스의 JWT 발급 및 로그인 정보 반환
            String serviceAccessToken = BEARER_PREFIX + jwtUtil.createAccessToken(user.getEmail());
            String serviceRefreshToken = jwtUtil.createRefreshToken(user.getEmail());

            // Redis에 Refresh Token 저장
            redisTemplate.opsForValue().set(
                    user.getEmail(),
                    serviceRefreshToken,
                    refreshTokenExpiration,
                    TimeUnit.MILLISECONDS
            );

            return LoginResponseDto.builder()
                    .userId(user.getUserId())
                    .nickname(user.getNickname())
                    .gender(user.getGender())
                    .birthDate(user.getBirthDate())
                    .notificationEnabled(user.isNotificationEnabled())
                    .broPoint(user.getBroPoint())
                    .broLevel(user.getBroLevel())
                    .accessToken(serviceAccessToken)
                    .refreshToken(serviceRefreshToken)
                    .build();
        }
    }

    private UserUpsertResult upsertUser(OAuth2UserInfo userInfo) {
        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());

        User user;
        boolean isNewUser = false;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getProvider() == null) {
                user.setProvider(userInfo.getProvider());
                user.setProviderId(userInfo.getProviderId());
            }
        } else {
            isNewUser = true;
            user = User.builder()
                    .email(userInfo.getEmail())
                    .userName(userInfo.getName())
                    .provider(userInfo.getProvider())
                    .providerId(userInfo.getProviderId())
                    .build();
        }
        return new UserUpsertResult(user, isNewUser);
    }

    // 결과 전달용 record
    private record UserUpsertResult(User user, boolean isNewUser) {}
}