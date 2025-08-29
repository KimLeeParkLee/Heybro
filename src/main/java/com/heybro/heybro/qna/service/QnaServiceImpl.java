package com.heybro.heybro.qna.service;

import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.qna.domain.Answer;
import com.heybro.heybro.qna.domain.Question;
import com.heybro.heybro.qna.domain.QuestionImage;
import com.heybro.heybro.qna.dto.response.QuestionImageResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import com.heybro.heybro.qna.repository.AnswerRepository;
import com.heybro.heybro.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaServiceImpl implements QnaService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private Sort getSort(String sort) {
        if (sort == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        switch (sort) {
            case "view_desc":
                return Sort.by(Sort.Direction.DESC, "viewCount");
            case "view_asc":
                return Sort.by(Sort.Direction.ASC, "viewCount");
            case "created_desc":
                return Sort.by(Sort.Direction.DESC, "createdAt");
            case "created_asc":
                return Sort.by(Sort.Direction.ASC, "createdAt");
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }

    @Override
    public QuestionResponseDto getQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new ResourceNotFoundException("해당 아이디를 가진 질문을 찾을 수 없습니다: " + questionId)
        );

        List<Answer> answers = answerRepository.findAllByQuestion(question);

        return QuestionResponseDto.builder()
                .questionId(questionId)
                .title(question.getTitle())
                .content(question.getContent())
                .userId(question.getUser().getId())
                .nickname(question.getUser().getNickname())
                .answerCount(answers.size())
                .viewCount(question.getViewCount())
                .createdAt(question.getCreatedAt())
                .category(question.getQuestionCategory())
                .tags(question.getTags())
                .questionImages(question.getQuestionImages() // List<QuestionImage> 가져오기
                        .stream()               // Stream<QuestionImage>으로 변환
                        .sorted(Comparator.comparing(QuestionImage::getSortOrder)) // sortOrder 순으로 오름차순 정렬
                        .map(QuestionImageResponseDto::from) // 각 QuestionImage를 DTO로 변환
                        .toList())
                .answers(answers.stream().map(QuestionResponseDto.AnswerResponseDto::from) // 각 QuestionImage를 DTO로 변환
                        .toList())
                .build();
    }
}
