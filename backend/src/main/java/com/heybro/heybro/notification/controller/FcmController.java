package com.heybro.heybro.notification.controller;

import com.heybro.heybro.notification.dto.request.FcmSendDto;
import com.heybro.heybro.notification.service.FcmService;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
@Tag(name = "알림", description = "알림 API")
public class FcmController {
    private final FcmService fcmService;
    private final UserRepository userRepository;

    // 이전에는 요청 본문의 targetToken 으로 아무 기기에나 보낼 수 있었다. 로그인한 회원이 등록한 자기 토큰으로만 보낸다.
    @Operation(summary = "내 기기로 테스트 알림 발송")
    @PostMapping("/send")
    public ResponseEntity<String> sendFcmMessage(@RequestBody FcmSendDto fcmSendDto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + userDetails.getUsername()));
        if (user.getNotificationToken() == null || user.getNotificationToken().isBlank()) {
            throw new IllegalArgumentException("등록된 알림 토큰이 없습니다.");
        }
        fcmService.sendMessage(FcmSendDto.builder()
                .targetToken(user.getNotificationToken())
                .title(fcmSendDto.getTitle())
                .body(fcmSendDto.getBody())
                .build());
        return ResponseEntity.ok("FCM message sent successfully.");
    }
}
