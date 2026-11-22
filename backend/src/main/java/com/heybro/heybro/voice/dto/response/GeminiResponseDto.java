package com.heybro.heybro.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Google Gemini API의 응답을 받기 위한 DTO입니다.
 * 응답 JSON의 'candidates' 필드에 결과가 포함됩니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true) // 응답의 모든 필드를 매핑할 필요 없도록 설정
public record GeminiResponseDto(
        List<Candidate> candidates
) {
    public record Candidate(
            Content content,
            String finishReason,
            int index,
            List<SafetyRating> safetyRatings
    ) {}

    public record Content(
            List<Part> parts,
            String role
    ) {}

    public record Part(
            String text
    ) {}

    public record SafetyRating(
            String category,
            String probability
    ) {}
}
