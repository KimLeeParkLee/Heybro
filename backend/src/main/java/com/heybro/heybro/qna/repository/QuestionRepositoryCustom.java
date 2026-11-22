package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.dto.QuestionSearchCondition;
import com.heybro.heybro.qna.dto.response.QuestionListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {
    Page<QuestionListResponseDto> searchQuestions(QuestionSearchCondition condition, Pageable pageable);
}