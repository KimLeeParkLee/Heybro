package com.heybro.heybro.qna.service;

import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.common.s3.service.S3UploadService;
import com.heybro.heybro.qna.domain.*;
import com.heybro.heybro.qna.dto.QuestionSearchCondition;
import com.heybro.heybro.qna.dto.request.AnswerRequestDto;
import com.heybro.heybro.qna.dto.request.QuestionRequestDto;
import com.heybro.heybro.qna.dto.response.CustomPageResponse;
import com.heybro.heybro.qna.dto.response.QuestionIdResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionListResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import com.heybro.heybro.qna.repository.AnswerRepository;
import com.heybro.heybro.qna.repository.QuestionRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaServiceImpl implements QnaService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final QuestionViewService questionViewService;

    @Override
    public CustomPageResponse<QuestionListResponseDto> getQuestions(QuestionCategory category, String search, String sort, Pageable pageable) {
        // 1. 검색 조건에 맞는 질문 페이지를 조회
        QuestionSearchCondition condition = new QuestionSearchCondition(category, search, sort);

        // 2. Repository에서 DTO로 직접 페이지 조회
        Page<QuestionListResponseDto> questionPage = questionRepository.searchQuestions(condition, pageable);

        // 3. CustomPageResponse 로 반환 (원하는 필드만 담음)
        return new CustomPageResponse<>(
                questionPage.getContent(),
                questionPage.isLast(),
                questionPage.getSize(),
                questionPage.getNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestion(Long questionId) {
        // 1. 조회수 증가 로직을 별도 트랜잭션으로 실행
        questionViewService.incrementViewCount(questionId);

        // 2. 질문과 관련된 대부분의 정보를 Fetch Join으로 한 번에 조회
        Question question = questionRepository.findByIdWithDetails(questionId).orElseThrow(
                () -> new ResourceNotFoundException("해당 아이디를 가진 질문을 찾을 수 없습니다: " + questionId)
        );

        // 3. 답변과 답변 작성자 정보를 Fetch Join으로 한 번에 조회
        List<Answer> answers = answerRepository.findAnswersByQuestionIdWithUser(questionId);

        // 4. DTO 변환
        return QuestionResponseDto.from(question, answers);
    }

    @Override
    @Transactional
    public QuestionIdResponseDto createQuestion(QuestionRequestDto requestDto, List<MultipartFile> images, String email) {
        // 1. 회원 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 2. Question 엔티티 생성
        Question question = Question.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .createdAt(LocalDateTime.now())
                .categories(requestDto.getCategories())
                .user(user)
                .build();

        // 태그 처리
        if (requestDto.getTags() != null) {
            for (Tag tag : requestDto.getTags()) {
                question.addTag(tag); // addTag에서 question 필드 자동 설정
            }
        }

        String thumbnailImageUrl = null;

        // 3. 이미지 파일 처리
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);

                // 로그 찍기
                log.info("images[{}] 확인: isEmpty={}, originalFilename='{}'", i, image.isEmpty(), image.getOriginalFilename());

                if (image == null || image.isEmpty() || image.getOriginalFilename() == null || image.getOriginalFilename().isBlank()) {
                    log.warn("images[{}] 파일이 비어있거나 잘못됨, 건너뜀", i);
                    continue;
                }

                try {
                    String imageUrl = s3UploadService.saveFile(image);
                    log.info("images[{}] 업로드 완료: {}", i, imageUrl);

                    if (i == 0) {
                        thumbnailImageUrl = imageUrl;
                    }

                    QuestionImage questionImage = QuestionImage.builder()
                            .questionImage(imageUrl)
                            .sortOrder(i + 1)
                            .question(question)
                            .build();
                    question.addQuestionImage(questionImage);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
                }
            }
        }

        // 썸네일 URL이 있다면 Question 객체에 설정
        if (thumbnailImageUrl != null) {
            question.updateThumbnail(thumbnailImageUrl);
        }

        // 4. Question 엔티티 저장 (QuestionImage는 Cascade 옵션으로 자동 저장)
        Question savedQuestion = questionRepository.save(question);

        // 5. 생성된 Question의 ID를 DTO로 감싸서 반환
        return new QuestionIdResponseDto(savedQuestion.getId());
    }

    @Override
    @Transactional
    public QuestionIdResponseDto createAnswer(Long questionId, AnswerRequestDto requestDto, List<MultipartFile> images, String email) {
        // 1. 회원 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 2. 질문 정보 조회
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 아이디를 가진 질문을 찾을 수 없습니다: " + questionId));

        // 3. Question 엔티티 생성
        Answer answer = Answer.builder()
                .content(requestDto.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                .question(question)
                .build();

        // 4. 이미지 파일 처리
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);

                if (image == null || image.isEmpty() || image.getOriginalFilename() == null || image.getOriginalFilename().isBlank()) {
                    continue;
                }

                try {
                    String imageUrl = s3UploadService.saveFile(image);

                    AnswerImage answerImage = AnswerImage.builder()
                            .answerImage(imageUrl)
                            .sortOrder(i + 1)
                            .answer(answer)
                            .build();
                    answer.addAnswerImage(answerImage);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
                }
            }
        }

        // 4. Question 엔티티 저장 (QuestionImage는 Cascade 옵션으로 자동 저장)
        Answer savedAnswer = answerRepository.save(answer);

        // 5. 생성된 Question의 ID를 DTO로 감싸서 반환q
        return new QuestionIdResponseDto(savedAnswer.getQuestion().getId());
    }
}
