package com.heybro.heybro.qna.service;

import com.heybro.heybro.qna.domain.QuestionCategory;
import com.heybro.heybro.qna.dto.request.AnswerRequestDto;
import com.heybro.heybro.qna.dto.request.QuestionRequestDto;
import com.heybro.heybro.qna.dto.response.CustomPageResponse;
import com.heybro.heybro.qna.dto.response.QuestionIdResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionListResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public interface QnaService {
    CustomPageResponse<QuestionListResponseDto> getQuestions(QuestionCategory category, String search, String sort, Pageable pageable);

    QuestionResponseDto getQuestion(Long questionId);

    QuestionIdResponseDto createQuestion(QuestionRequestDto requestDto, List<MultipartFile> images, String email);

    QuestionIdResponseDto createAnswer(Long questionId, AnswerRequestDto requestDto, List<MultipartFile> images, String email);
}
