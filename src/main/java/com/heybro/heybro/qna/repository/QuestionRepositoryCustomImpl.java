package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.QAnswer;
import com.heybro.heybro.qna.domain.QuestionCategory;
import com.heybro.heybro.qna.dto.QuestionSearchCondition;
import com.heybro.heybro.qna.dto.response.QQuestionListResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionListResponseDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.heybro.heybro.qna.domain.QQuestion.question;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<QuestionListResponseDto> searchQuestions(QuestionSearchCondition condition, Pageable pageable) {
        // 1단계: ID와 정렬 기준 컬럼을 함께 조회 (Tuple 사용)
        List<Tuple> tuples = queryFactory
                .select(question.id, question.createdAt, question.viewCount)
                .from(question)
                .where(
                        categoryEq(condition.getCategory()),
                        searchContains(condition.getSearch())
                )
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 조회된 ID 목록만 추출
        List<Long> ids = tuples.stream()
                .map(tuple -> tuple.get(question.id))
                .toList();

        if (ids.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 2단계: ID 기준으로 DTO 조회
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
                .fetch();

        // ids 순서를 content에도 반영
        content = content.stream()
                .sorted((a, b) -> {
                    int idxA = ids.indexOf(a.getQuestionId());
                    int idxB = ids.indexOf(b.getQuestionId());
                    return Integer.compare(idxA, idxB);
                })
                .toList();

        // 3단계: Count 쿼리
        Long total = queryFactory
                .select(question.count())
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

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        if (sort.isUnsorted()) {
            return new OrderSpecifier[]{new OrderSpecifier<>(Order.DESC, question.createdAt)};
        }
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        sort.forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();
            PathBuilder pathBuilder = new PathBuilder<>(question.getType(), question.getMetadata());
            orders.add(new OrderSpecifier(direction, pathBuilder.get(prop)));
        });
        return orders.toArray(OrderSpecifier[]::new);
    }
}