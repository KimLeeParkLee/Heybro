package com.heybro.heybro.user.service;

import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.dto.request.UserRegistrationRequestDto;
import com.heybro.heybro.user.dto.response.UserRegistrationResponseDto;
import com.heybro.heybro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponseDto registerNewUser(UserRegistrationRequestDto requestDto) {
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
}
