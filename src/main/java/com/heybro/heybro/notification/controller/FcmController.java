package com.heybro.heybro.notification.controller;

import com.heybro.heybro.notification.dto.request.FcmSendDto;
import com.heybro.heybro.notification.service.FcmService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
@Tag(name = "파일 업로드", description = "파일 업로드 API")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/send")
    public ResponseEntity<String> sendFcmMessage(@RequestBody FcmSendDto fcmSendDto) {
        fcmService.sendMessage(fcmSendDto);
        return ResponseEntity.ok("FCM message sent successfully.");
    }
}