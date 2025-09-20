package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.QAnswer;
import com.heybro.heybro.qna.domain.QuestionCategory;
import com.heybro.heybro.qna.dto.QuestionSearchCondition;
import com.heybro.heybro.qna.dto.response.QQuestionListResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionListResponseDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.heybro.heybro.qna.domain.QQuestion.question;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<QuestionListResponseDto> searchQuestions(QuestionSearchCondition condition, Pageable pageable) {
        // 1단계: 조건에 맞는 Question의 ID 목록만 페이징하여 조회
        List<Long> ids = queryFactory
                .select(question.id)
                .from(question)
                .where(
                        categoryEq(condition.getCategory()),
                        searchContains(condition.getSearch())
                )
                .groupBy(question.id)
                .orderBy(sort(condition.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 조회된 ID가 없으면 빈 페이지를 즉시 반환
        if (ids.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 2단계: 조회된 ID 목록을 사용하여 IN 절로 DTO 데이터를 한 번에 조회
        QAnswer answer = QAnswer.answer;
        List<QuestionListResponseDto> content = queryFactory
                .select(new QQuestionListResponseDto(
                        question.id,
                        question.title,
                        question.content,
                        question.user.id,
                        question.user.nickname,
                        question.viewCount,
                        question.createdAt,
                        question.thumbnail,
                        JPAExpressions
                                .select(answer.count())
                                .from(answer)
                                .where(answer.question.eq(question))
                ))
                .from(question)
                .where(question.id.in(ids))
                .orderBy(sort(condition.getSort()))
                .fetch();

        // 3단계: Count 쿼리 별도 실행
        Long total = queryFactory
                .select(question.countDistinct())
                .from(question)
                .where(
                        categoryEq(condition.getCategory()),
                        searchContains(condition.getSearch())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression categoryEq(QuestionCategory category) {
        return category != null ? question.categories.contains(category) : null;
    }

    private BooleanExpression searchContains(String search) {
        return search != null ? question.title.containsIgnoreCase(search).or(question.content.containsIgnoreCase(search)) : null;
    }

    private OrderSpecifier<?> sort(String sort) {
        if (sort == null) {
            return question.createdAt.desc();
        }
        return switch (sort) {
            case "latest" -> question.createdAt.desc();
            case "views" -> question.viewCount.desc();
            default -> question.createdAt.desc();
        };
    }
}