package com.heybro.heybro.routine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "추천 상품 응답 DTO")
public class RecommendProductResponseDto {
    @Schema(description = "상품 이름")
    private String name;

    @Schema(description = "상품 이미지")
    private String image;

    @Schema(description = "자사몰 링크")
    private String link;
}
