package com.heybro.heybro.user.dto.response;

import com.heybro.heybro.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 랭킹 응답 DTO")
public class UserRankingResponseDto {
    @Builder.Default
    @Schema(description = "랭킹 목록")
    List<RankingResponseDto> rankings = new ArrayList<>();

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "랭킹")
    public static class RankingResponseDto {
        @Schema(description = "순위")
        private int rank;

        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "누적 브로 포인트")
        private long totalBroPoint;

        @Schema(description = "프로필 사진")
        private String profileImage;

        public static RankingResponseDto from(User user, int ranking) {
            return RankingResponseDto.builder()
                    .rank(ranking)
                    .nickname(user.getNickname())
                    .totalBroPoint(user.getTotalBroPoint())
                    .profileImage(user.getProfileImage())
                    .build();
        }
    }

    @Schema(description = "나의 랭킹")
    private RankingResponseDto myRanking;

    public static UserRankingResponseDto from(List<com.heybro.heybro.user.repository.UserRankingProjection> top10Rankings, User user, int myRanking) {
        return UserRankingResponseDto.builder()
                .rankings(top10Rankings.stream()
                        .map(projection -> RankingResponseDto.builder()
                                .rank(projection.getRanking())
                                .nickname(projection.getNickname())
                                .totalBroPoint(projection.getTotalBroPoint())
                                .profileImage(projection.getProfileImage())
                                .build())
                        .toList())
                .myRanking(RankingResponseDto.from(user, myRanking))
                .build();
    }
}
