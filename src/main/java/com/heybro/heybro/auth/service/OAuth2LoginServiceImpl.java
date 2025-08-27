package com.heybro.heybro.auth.service;

import com.heybro.heybro.auth.client.OAuth2Client;
import com.heybro.heybro.auth.dto.OAuth2UserInfo;
import com.heybro.heybro.auth.dto.request.OAuth2LoginRequestDto;
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
    public Mono<Object> oauth2Login(OAuth2LoginRequestDto requestDto) {
        OAuth2Client oAuth2Client = oAuth2Clients.get(requestDto.getProvider().toLowerCase());

        if (oAuth2Client == null) {
            return Mono.error(new IllegalArgumentException("Unsupported provider: " + requestDto.getProvider()));
        }

        return oAuth2Client.getAccessToken(requestDto.getOauthToken())
                .flatMap(accessToken -> oAuth2Client.getUserInfoByToken(accessToken))
                .flatMap(userInfo -> {
                    UserUpsertResult result = upsertUser(userInfo);
                    User user = result.user();

                    if (result.isNewUser()) {
                        return Mono.just(OAuth2SignUpResponseDto.builder()
                                .email(user.getEmail())
                                .provider(user.getProvider())
                                .build());
                    } else {
                        String serviceAccessToken = BEARER_PREFIX + jwtUtil.createAccessToken(user.getEmail());
                        String serviceRefreshToken = jwtUtil.createRefreshToken(user.getEmail());

                        redisTemplate.opsForValue().set(
                                user.getEmail(),
                                serviceRefreshToken,
                                refreshTokenExpiration,
                                TimeUnit.MILLISECONDS
                        );

                        return Mono.just(LoginResponseDto.builder()
                                .userId(user.getId())
                                .nickname(user.getNickname())
                                .gender(user.getGender())
                                .birthDate(user.getBirthDate())
                                .notificationEnabled(user.isNotificationEnabled())
                                .broPoint(user.getBroPoint())
                                .broLevel(user.getBroLevel())
                                .accessToken(serviceAccessToken)
                                .refreshToken(serviceRefreshToken)
                                .build());
                    }
                });
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
                    .name(userInfo.getName())
                    .provider(userInfo.getProvider())
                    .providerId(userInfo.getProviderId())
                    .build();
        }
        userRepository.save(user);
        return new UserUpsertResult(user, isNewUser);
    }

    private record UserUpsertResult(User user, boolean isNewUser) {}
}