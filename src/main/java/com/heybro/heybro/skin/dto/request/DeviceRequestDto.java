package com.heybro.heybro.skin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "디바이스 정보 요청 DTO")
public class DeviceRequestDto {
    @Schema(description = "플랫폼", example = "rn")
    private String platform;

    @Schema(description = "디바이스 모델명", example = "iPhone 14")
    private String model;
}
