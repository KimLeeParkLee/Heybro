package com.heybro.heybro.qna.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "질문 목록 응답 DTO")
public class QuestionListResponseDto {
    @Schema(description = "질문 식별키")
    private Long questionId;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "작성자 ID")
    private Long userId;

    @Schema(description = "작성자 닉네임")
    private String nickname;

    @Schema(description = "조회수")
    private int viewCount;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;

    @Schema(description = "대표 사진")
    private String thumbnail;

    @Schema(description = "답변 개수")
    private long answerCount;

    @QueryProjection
    public QuestionListResponseDto(Long questionId, String title, String content, Long userId, String nickname, int viewCount, LocalDateTime createdAt, String thumbnail, long answerCount) {
        this.questionId = questionId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.nickname = nickname;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.thumbnail = thumbnail;
        this.answerCount = answerCount;
    }
}
