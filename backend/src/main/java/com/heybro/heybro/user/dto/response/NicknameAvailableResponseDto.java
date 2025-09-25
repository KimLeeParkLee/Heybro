package com.heybro.heybro.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "닉네임 가능 여부 응답 DTO")
public class NicknameAvailableResponseDto {
    @Schema(description = "닉네임 가능 여부")
    private boolean isAvailable;
}
