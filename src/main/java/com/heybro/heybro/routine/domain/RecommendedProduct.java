package com.heybro.heybro.routine.domain;

import com.heybro.heybro.routine.dto.response.RecommendProductResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RecommendedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 추천 상품 식별키

    private String name; // 상품 이름

    private String image; // 상품 이미지

    private String link; // 자사몰 링크

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine; // 루틴

    public static RecommendProductResponseDto from(RecommendedProduct product) {
        return RecommendProductResponseDto.builder()
                .name(product.getName())
                .image(product.getImage())
                .link(product.getLink())
                .build();
    }

    public void updateRoutine(Routine routine) {
        this.routine = routine;
    }
}
