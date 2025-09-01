package com.heybro.heybro.notification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmSendDto {
    private String targetToken; // 알림을 받을 대상의 FCM 토큰
    private String title;
    private String body;
}