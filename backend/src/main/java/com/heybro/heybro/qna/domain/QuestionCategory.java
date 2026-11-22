package com.heybro.heybro.qna.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QuestionCategory {
    SKIN_CONCERN("피부 고민"),
    PRODUCT_RECOMMENDATION("제품 추천"),
    ROUTINE_SHARE("루틴 공유"),
    FRAGRANCE("향 & 채취"),
    FASHION_STYLE("패션 & 스타일링"),
    SHAVING_HAIRCARE("면도 & 헤어관리"),
    HEALTH("건강");

    private final String description;
}
