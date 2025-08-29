package com.heybro.heybro.qna.service;

import com.heybro.heybro.qna.dto.response.QuestionListResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface QnaService {
    //Page<QuestionListResponseDto> getQuestions(String sort, Long categoryId, int page, int size, String search);

    QuestionResponseDto getQuestion(Long questionId);
}
