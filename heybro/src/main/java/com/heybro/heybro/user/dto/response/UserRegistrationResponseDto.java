package com.heybro.heybro.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 응답 DTO")
public class UserRegistrationResponseDto {
    @Schema(description = "회원 식별키")
    private Long userId;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "성별")
    private String gender;

    @Schema(description = "생년월일")
    private Timestamp birthDate;

    @Schema(description = "알림 설정 여부")
    private boolean notificationEnabled;

    @Schema(description = "브로 포인트")
    private int broPoint;

    @Schema(description = "브로 레벨")
    private int broLevel;
}
