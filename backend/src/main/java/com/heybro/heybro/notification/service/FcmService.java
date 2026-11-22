package com.heybro.heybro.notification.service;

import com.google.firebase.messaging.*;
import com.heybro.heybro.notification.dto.request.FcmSendDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FcmService {
    // FCM sendEach 는 한 번에 최대 500건
    private static final int FCM_BATCH_SIZE = 500;

    // 여러 건을 500개 단위로 묶어 보낸다. 건마다 HTTP 왕복하던 방식보다 호출 수가 1/500 로 줄고, 한 건 실패가 나머지에 영향을 주지 않는다.
    public int sendMessages(List<FcmSendDto> fcmSendDtos) {
        int success = 0;
        for (int from = 0; from < fcmSendDtos.size(); from += FCM_BATCH_SIZE) {
            List<Message> messages = fcmSendDtos.subList(from, Math.min(from + FCM_BATCH_SIZE, fcmSendDtos.size()))
                    .stream()
                    .map(this::buildMessage)
                    .toList();
            try {
                BatchResponse response = FirebaseMessaging.getInstance().sendEach(messages);
                success += response.getSuccessCount();
                if (response.getFailureCount() > 0) {
                    log.warn("FCM 일괄 발송 실패 {}건 / {}건", response.getFailureCount(), messages.size());
                }
            } catch (FirebaseMessagingException e) {
                log.error("FCM 일괄 발송 중 오류: {}", e.getMessage());
            }
        }
        return success;
    }

    public void sendMessage(FcmSendDto fcmSendDto) {
        try {
            String response = FirebaseMessaging.getInstance().send(buildMessage(fcmSendDto));
            log.info("FCM 발송 완료: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 발송 실패: {}", e.getMessage());
        }
    }

    private Message buildMessage(FcmSendDto fcmSendDto) {
        // iOS 알림에 특화된 설정
        Aps aps = Aps.builder()
                .setSound("default") // 알림 수신 시 소리
                .setBadge(1)         // 앱 아이콘에 표시될 배지 숫자
                .build();

        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(aps)
                .build();

        Notification notification = Notification.builder()
                .setTitle(fcmSendDto.getTitle())
                .setBody(fcmSendDto.getBody())
                .build();

        return Message.builder()
                .setToken(fcmSendDto.getTargetToken()) // 필수: 알림을 보낼 대상 토큰
                .setNotification(notification)
                .setApnsConfig(apnsConfig) // iOS를 위한 별도 설정 추가
                .build();
    }
}
