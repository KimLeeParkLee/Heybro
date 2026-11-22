package com.heybro.heybro.qna.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.heybro.heybro.qna.dto.response.QQuestionListResponseDto is a Querydsl Projection type for QuestionListResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QQuestionListResponseDto extends ConstructorExpression<QuestionListResponseDto> {

    private static final long serialVersionUID = -187028621L;

    public QQuestionListResponseDto(com.querydsl.core.types.Expression<Long> questionId, com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<String> content, com.querydsl.core.types.Expression<Long> userId, com.querydsl.core.types.Expression<String> nickname, com.querydsl.core.types.Expression<Integer> viewCount, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt, com.querydsl.core.types.Expression<String> thumbnail, com.querydsl.core.types.Expression<Long> answerCount) {
        super(QuestionListResponseDto.class, new Class<?>[]{long.class, String.class, String.class, long.class, String.class, int.class, java.time.LocalDateTime.class, String.class, long.class}, questionId, title, content, userId, nickname, viewCount, createdAt, thumbnail, answerCount);
    }

}

