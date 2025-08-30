package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.Question;
import com.heybro.heybro.qna.domain.QuestionCategory;
import com.heybro.heybro.qna.dto.QuestionSearchCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.heybro.heybro.qna.domain.QQuestion.question;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Question> searchQuestions(QuestionSearchCondition condition, Pageable pageable) {
        // 1. 데이터 조회를 위한 쿼리
        List<Question> content = queryFactory
                .selectFrom(question)
                .where(
                        categoryEquals(condition.getCategory()),
                        searchContains(condition.getSearch())
                )
                .orderBy(createOrderSpecifier(condition.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 전체 카운트를 위한 쿼리 (최적화를 위해 분리)
        JPAQuery<Long> countQuery = queryFactory
                .select(question.count())
                .from(question)
                .where(
                        categoryEquals(condition.getCategory()),
                        searchContains(condition.getSearch())
                );

        // 3. Page 객체 생성 후 반환
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // == 동적 WHERE 조건을 위한 메소드들 ==
    private BooleanExpression categoryEquals(QuestionCategory category) {
        return category != null ? question.categories.contains(category) : null;
    }

    private BooleanExpression searchContains(String search) {
        return StringUtils.hasText(search) ? question.title.containsIgnoreCase(search).or(question.content.containsIgnoreCase(search)) : null;
    }

    // == 커스텀 Sort 처리를 위한 메소드 ==
    private OrderSpecifier<?> createOrderSpecifier(String sort) {
        if (!StringUtils.hasText(sort)) {
            return question.createdAt.desc(); // 기본 정렬
        }

        if ("view_desc".equals(sort)) {
            return question.viewCount.desc();
        }

        return question.createdAt.desc(); // 매칭되는 조건 없으면 기본 정렬
    }
}
