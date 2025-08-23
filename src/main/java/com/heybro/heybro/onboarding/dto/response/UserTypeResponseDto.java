package com.heybro.heybro.onboarding.dto.response;

import com.heybro.heybro.user.domain.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 유형 응답 DTO")
public class UserTypeResponseDto {
    @Enumerated(EnumType.STRING)
    @Schema(description = "회원 유형")
    private UserType userType;

    public static UserTypeResponseDto from(UserType userType) {
        // UserType Enum 객체를 받아서 DTO 객체를 생성하고 반환
        return UserTypeResponseDto.builder()
                .userType(userType) // Enum의 이름 (예: "DRY_OFFICE")
                .build();
    }
}
