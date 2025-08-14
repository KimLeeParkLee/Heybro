package com.heybro.heybro.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class UserRegistrationRequestDto {
    @Schema(description = "이름")
    private String userName;

    @Schema(description = "닉네임")
    private String nickname;

    @NotNull
    @Schema(description = "이메일")
    private String email;

    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "성별")
    private String gender;

    @Schema(description = "생년월일")
    private Timestamp birthDate;

    @Schema(description = "핸드폰 번호")
    private String phone;

    @Schema(description = "개인정보 동의 여부")
    private boolean privacyConsent;

    @Schema(description = "마케팅 동의 여부")
    private boolean marketingConsent;

    @Schema(description = "알림 설정 여부")
    private boolean notificationEnabled;
}
