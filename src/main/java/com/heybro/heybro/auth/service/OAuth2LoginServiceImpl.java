package com.heybro.heybro.auth.service;

import com.heybro.heybro.auth.dto.request.OAuth2LoginRequestDto;
import com.heybro.heybro.auth.client.OAuth2Client;
import com.heybro.heybro.auth.dto.OAuth2UserInfo;
import com.heybro.heybro.auth.dto.response.OAuth2SignUpResponseDto;
import com.heybro.heybro.common.jwt.JwtUtil;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoleEnum;
import com.heybro.heybro.user.dto.response.LoginResponseDto;
import com.heybro.heybro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2LoginServiceImpl implements OAuth2LoginService {
    private final Map<String, OAuth2Client> oAuth2Clients;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public Object oauth2Login(OAuth2LoginRequestDto requestDto) {
        OAuth2Client oAuth2Client = oAuth2Clients.get(requestDto.getProvider().toLowerCase());

        if (oAuth2Client == null) {
            throw new IllegalArgumentException("Unsupported provider: " + requestDto.getProvider());
        }

        OAuth2UserInfo userInfo = oAuth2Client.getUserInfoByToken(requestDto.getOauthToken());
        UserUpsertResult result = upsertUser(userInfo);
        User user = result.user();

        if (result.isNewUser()) {
            // 신규 가입일 경우
            return OAuth2SignUpResponseDto.builder()
                    .isNewUser(true)
                    .email(user.getEmail())
                    .provider(user.getProvider())
                    .oauthToken(requestDto.getOauthToken()) // 추가 정보 입력을 위해 토큰 다시 전달
                    .build();
        } else {
            // 기존 회원 로그인일 경우
            String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole());
            String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getRole());

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
                    //.typeId(user.getTypeId())
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
                    .role(UserRoleEnum.USER)
                    .build();
            userRepository.save(user);
        }
        return new UserUpsertResult(user, isNewUser);
    }

    // 결과 전달용 record
    private record UserUpsertResult(User user, boolean isNewUser) {}
}