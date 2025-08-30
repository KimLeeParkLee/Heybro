package com.heybro.heybro.qna.service;

import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.common.s3.S3UploadService;
import com.heybro.heybro.qna.domain.*;
import com.heybro.heybro.qna.dto.request.AnswerRequestDto;
import com.heybro.heybro.qna.dto.request.QuestionRequestDto;
import com.heybro.heybro.qna.dto.request.TagRequestDto;
import com.heybro.heybro.qna.dto.response.QuestionIdResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionImageResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import com.heybro.heybro.qna.repository.AnswerRepository;
import com.heybro.heybro.qna.repository.QuestionRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaServiceImpl implements QnaService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

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
                .categories(question.getCategories().stream().toList())
                .tags(question.getTags().stream().map(TagRequestDto::from).toList())
                .questionImages(question.getQuestionImages() // List<QuestionImage> 가져오기
                        .stream()               // Stream<QuestionImage>으로 변환
                        .sorted(Comparator.comparing(QuestionImage::getSortOrder)) // sortOrder 순으로 오름차순 정렬
                        .map(QuestionImageResponseDto::from) // 각 QuestionImage를 DTO로 변환
                        .toList())
                .answers(answers.stream().map(QuestionResponseDto.AnswerResponseDto::from) // 각 QuestionImage를 DTO로 변환
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public QuestionIdResponseDto createQuestion(QuestionRequestDto requestDto, List<MultipartFile> images, String email) {
        // 1. 회원 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다: " + email));

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
                try {
                    String imageUrl = s3UploadService.saveFile(image);

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
    public QuestionIdResponseDto createAnswer(Long questionId, AnswerRequestDto requestDto, List<MultipartFile> images, String email) {
        // 1. 회원 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다: " + email));

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
