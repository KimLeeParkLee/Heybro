package com.heybro.heybro.skin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SkinScoreResponseDto {
    private ScoreInfo oldestScore;
    private ScoreInfo recentScore;
    private int scoreChange;

    @Getter
    @Builder
    public static class ScoreInfo {
        private int score;
        private LocalDateTime diagnosisDate;
        private int oilinessScore;
        private int hydrationScore;
        private int rednessScore;
    }
}