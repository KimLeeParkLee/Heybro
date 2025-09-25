package com.heybro.heybro.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Fcm 요청 DTO")
public class FcmSendDto {
    @Schema(description = "FCM 토큰")
    private String targetToken;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String body;
}