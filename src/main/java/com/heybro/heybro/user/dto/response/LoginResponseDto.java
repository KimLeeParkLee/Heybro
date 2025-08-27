package com.heybro.heybro.user.dto.response;

import com.heybro.heybro.user.domain.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {
    @Schema(description = "회원 식별키")
    private Long userId;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "성별")
    private String gender;

    @Schema(description = "생년월일")
    private LocalDate birthDate;

    @Schema(description = "알림 설정 여부")
    private boolean notificationEnabled;

    @Schema(description = "브로 포인트")
    private int broPoint;

    @Schema(description = "브로 레벨")
    private int broLevel;

    @Schema(description = "경험치")
    private int experience;

    @Schema(description = "access token")
    private String accessToken;

    @Schema(description = "refresh token")
    private String refreshToken;

    @Schema(description = "회원 유형")
    private UserType userType;
}
