package com.heybro.heybro.auth.service;

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
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
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
    public Mono<Object> loginWithAccessToken(String provider, String accessToken) {
        OAuth2Client oAuth2Client = oAuth2Clients.get(provider.toLowerCase());

        if (oAuth2Client == null) {
            return Mono.error(new IllegalArgumentException("Unsupported provider: " + provider));
        }

        return oAuth2Client.getUserInfoByToken(accessToken)
                .map(this::processSocialLogin);
    }

    @Override
    public Object loginWithIdentityToken(String provider, String identityToken) {
        OAuth2Client oAuth2Client = oAuth2Clients.get(provider.toLowerCase());

        if (oAuth2Client == null) {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }

        OAuth2UserInfo userInfo = oAuth2Client.getUserInfoByIdentityToken(identityToken);
        return processSocialLogin(userInfo);
    }

    private Object processSocialLogin(OAuth2UserInfo userInfo) {
        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String serviceAccessToken = BEARER_PREFIX + jwtUtil.createAccessToken(user.getEmail());
            String serviceRefreshToken = jwtUtil.createRefreshToken(user.getEmail());

            redisTemplate.opsForValue().set(
                    user.getEmail(),
                    serviceRefreshToken,
                    refreshTokenExpiration,
                    TimeUnit.MILLISECONDS
            );

            return LoginResponseDto.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .gender(user.getGender())
                    .birthDate(user.getBirthDate())
                    .notificationEnabled(user.isNotificationEnabled())
                    .broPoint(user.getBroPoint())
                    .broLevel(user.getBroLevel())
                    .experience(user.getExperience())
                    .accessToken(serviceAccessToken)
                    .refreshToken(serviceRefreshToken)
                    .userType(user.getUserType())
                    .build();
        } else {
            return OAuth2SignUpResponseDto.builder()
                    .email(userInfo.getEmail())
                    .provider(userInfo.getProvider())
                    .build();
        }
    }
}
