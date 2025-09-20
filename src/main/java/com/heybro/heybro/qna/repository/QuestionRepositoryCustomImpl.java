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
                .select(question.id, question.createdAt, question.viewCount) // <--- 핵심 변경점 1
                .from(question)
                .where(
                        categoryEq(condition.getCategory()),
                        searchContains(condition.getSearch())
                )
                // .groupBy()는 더 이상 필요 없습니다.
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 조회된 튜플 리스트에서 ID 목록만 추출
        List<Long> ids = tuples.stream()
                .map(tuple -> tuple.get(question.id))
                .toList(); // <--- 핵심 변경점 2

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
                .orderBy(getOrderSpecifiers(pageable.getSort())) // 최종 반환 순서를 위해 여기도 정렬이 필요합니다.
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

    // 이 메서드는 수정할 필요 없습니다.
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