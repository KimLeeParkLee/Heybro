package com.heybro.heybro.skin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "디바이스 정보 요청 DTO")
public class DeviceRequestDto {
    @Schema(description = "플랫폼")
    private String platform;

    @Schema(description = "디바이스 모델명")
    private String model;
}
