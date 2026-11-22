package com.heybro.heybro.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "FCM 토큰 업데이트 요청 DTO")
public class FcmTokenUpdateRequestDto {
    @Schema(description = "FCM 토큰")
    private String fcmToken;
}
