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
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final QuestionDetailService questionDetailService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public CustomPageResponse<QuestionListResponseDto> getQuestions(QuestionCategory category, String search, Pageable pageable) {
        // 1. 검색 조건에 맞는 질문 페이지를 조회
        QuestionSearchCondition condition = new QuestionSearchCondition(category, search);

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
    public QuestionResponseDto getQuestion(Long questionId) {
        // 트랜잭션 없이 두 트랜잭션 메서드를 순서대로 호출한다.
        // 바깥 트랜잭션이 커넥션을 쥔 채 안쪽(REQUIRES_NEW) 트랜잭션이 커넥션을 하나 더 요구하면
        // 요청당 커넥션 2개를 써서 부하 시 HikariCP 풀이 고갈되므로, 각 단계가 커넥션을 1개씩 순서대로 쓰게 한다.

        // 1. 조회수 증가 (쓰기 트랜잭션, 커밋 후 커넥션 반납)
        questionViewService.incrementViewCount(questionId);

        // 2. 질문 상세 조회 + DTO 변환 (읽기 전용 트랜잭션)
        return questionDetailService.getQuestionDetail(questionId);
    }

    // S3 업로드(외부 I/O)는 트랜잭션 밖에서 먼저 끝내고, DB 저장만 짧은 트랜잭션으로 묶는다.
    // 이전에는 메서드 전체가 @Transactional 이라 업로드 시간만큼 DB 커넥션을 점유했고, 롤백되면 S3 에 고아 파일이 남았다.
    @Override
    public QuestionIdResponseDto createQuestion(QuestionRequestDto requestDto, List<MultipartFile> images, String email) {
        // 1. 회원 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 2. 이미지 업로드 (트랜잭션 밖)
        List<String> imageUrls = uploadImages(images);

        try {
            // 3. DB 저장 (짧은 트랜잭션)
            return transactionTemplate.execute(status -> {
                Question question = Question.builder()
                        .title(requestDto.getTitle())
                        .content(requestDto.getContent())
                        .createdAt(LocalDateTime.now())
                        .categories(requestDto.getCategories())
                        .user(user)
                        .build();

                if (requestDto.getTags() != null) {
                    for (Tag tag : requestDto.getTags()) {
                        question.addTag(tag); // addTag에서 question 필드 자동 설정
                    }
                }

                for (int i = 0; i < imageUrls.size(); i++) {
                    question.addQuestionImage(QuestionImage.builder()
                            .questionImage(imageUrls.get(i))
                            .sortOrder(i + 1)
                            .question(question)
                            .build());
                }
                if (!imageUrls.isEmpty()) {
                    question.updateThumbnail(imageUrls.get(0));
                }

                Question savedQuestion = questionRepository.save(question);
                return new QuestionIdResponseDto(savedQuestion.getId());
            });
        } catch (RuntimeException e) {
            // DB 저장 실패 시 업로드한 파일을 지워 고아 파일을 남기지 않는다
            s3UploadService.deleteFiles(imageUrls);
            throw e;
        }
    }

    // 비어 있거나 잘못된 파일은 건너뛰고, 업로드된 URL 을 순서대로 반환
    private List<String> uploadImages(List<MultipartFile> images) {
        List<String> urls = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            return urls;
        }
        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            if (image == null || image.isEmpty() || image.getOriginalFilename() == null || image.getOriginalFilename().isBlank()) {
                log.warn("images[{}] 파일이 비어있거나 잘못됨, 건너뜀", i);
                continue;
            }
            try {
                urls.add(s3UploadService.saveFile(image));
            } catch (IOException e) {
                s3UploadService.deleteFiles(urls);
                throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
            }
        }
        return urls;
    }

    @Override
    public QuestionIdResponseDto createAnswer(Long questionId, AnswerRequestDto requestDto, List<MultipartFile> images, String email) {
        // 1. 회원 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 2. 질문 정보 조회
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 아이디를 가진 질문을 찾을 수 없습니다: " + questionId));

        // 3. 이미지 업로드 (트랜잭션 밖)
        List<String> imageUrls = uploadImages(images);

        try {
            // 4. DB 저장 (짧은 트랜잭션)
            return transactionTemplate.execute(status -> {
                Answer answer = Answer.builder()
                        .content(requestDto.getContent())
                        .createdAt(LocalDateTime.now())
                        .user(user)
                        .question(question)
                        .build();

                for (int i = 0; i < imageUrls.size(); i++) {
                    answer.addAnswerImage(AnswerImage.builder()
                            .answerImage(imageUrls.get(i))
                            .sortOrder(i + 1)
                            .answer(answer)
                            .build());
                }

                Answer savedAnswer = answerRepository.save(answer);
                return new QuestionIdResponseDto(savedAnswer.getQuestion().getId());
            });
        } catch (RuntimeException e) {
            s3UploadService.deleteFiles(imageUrls);
            throw e;
        }
    }
}
