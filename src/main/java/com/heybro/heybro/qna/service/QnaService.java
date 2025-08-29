package com.heybro.heybro.qna.service;

import com.heybro.heybro.qna.dto.request.QuestionRequestDto;
import com.heybro.heybro.qna.dto.response.QuestionIdResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface QnaService {
    //Page<QuestionListResponseDto> getQuestions(String sort, Long categoryId, int page, int size, String search);

    QuestionResponseDto getQuestion(Long questionId);

    QuestionIdResponseDto createQuestion(QuestionRequestDto requestDto, List<MultipartFile> images, String email);
}
