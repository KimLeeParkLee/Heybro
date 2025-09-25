package com.heybro.heybro.notification.service;

import com.google.firebase.messaging.*;
import com.heybro.heybro.notification.dto.request.FcmSendDto;
import org.springframework.stereotype.Service;

@Service
public class FcmService {

    public void sendMessage(FcmSendDto fcmSendDto) {
        // iOS 알림에 특화된 설정을 추가합니다.
        Aps aps = Aps.builder()
                .setSound("default") // 알림 수신 시 소리
                .setBadge(1)         // 앱 아이콘에 표시될 배지 숫자
                .build();

        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(aps)
                .build();

        // 알림 내용 설정
        Notification notification = Notification.builder()
                .setTitle(fcmSendDto.getTitle())
                .setBody(fcmSendDto.getBody())
                .build();

        // 메시지 최종 구성
        Message message = Message.builder()
                .setToken(fcmSendDto.getTargetToken()) // 필수: 알림을 보낼 대상 토큰
                .setNotification(notification)
                .setApnsConfig(apnsConfig) // iOS를 위한 별도 설정 추가
                .build();

        try {
            // 메시지 발송
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Failed to send FCM message.");
            e.printStackTrace();
        }
    }
}