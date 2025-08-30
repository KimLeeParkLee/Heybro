package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.Question;
import com.heybro.heybro.qna.dto.QuestionSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepositoryCustom {
    Page<Question> searchQuestions(QuestionSearchCondition condition, Pageable pageable);
}
