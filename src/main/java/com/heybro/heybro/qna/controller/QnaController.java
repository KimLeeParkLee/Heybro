package com.heybro.heybro.qna.controller;

import com.heybro.heybro.qna.domain.QuestionCategory;
import com.heybro.heybro.qna.dto.request.AnswerRequestDto;
import com.heybro.heybro.qna.dto.request.QuestionRequestDto;
import com.heybro.heybro.qna.dto.response.CustomPageResponse;
import com.heybro.heybro.qna.dto.response.QuestionIdResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionListResponseDto;
import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import com.heybro.heybro.qna.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
@Tag(name = "Q&A", description = "Q&A API")
@Slf4j
public class QnaController {
    private final QnaService qnaService;

    @Operation(summary = "질문 목록 조회")
    @GetMapping
    public CustomPageResponse<QuestionListResponseDto> searchQuestions(@RequestParam(required = false) QuestionCategory category, @RequestParam(required = false) String search,
                                                                       @RequestParam(required = false) String sort, @PageableDefault(size = 10) Pageable pageable) {
        return qnaService.getQuestions(category, search, sort, pageable);
    }

    @Operation(summary = "질문 상세 조회")
    @GetMapping("/{question_id}")
    public QuestionResponseDto getQuestion(@PathVariable Long question_id) {
        return qnaService.getQuestion(question_id);
    }

    @Operation(summary = "질문 작성")
    @PostMapping
    public QuestionIdResponseDto createQuestion(@RequestPart("requestDto") QuestionRequestDto requestDto, @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        if (images == null) {
            images = Collections.emptyList();
        }
        return qnaService.createQuestion(requestDto, images, userDetails.getUsername());
    }

    @Operation(summary = "답변 작성")
    @PostMapping(path = "/{question_id}/answers", consumes = {"multipart/form-data"})
    public QuestionIdResponseDto createAnswer(@PathVariable Long question_id, @RequestPart("requestDto") AnswerRequestDto requestDto, @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                              @AuthenticationPrincipal UserDetails userDetails) {

        return qnaService.createAnswer(question_id, requestDto, images, userDetails.getUsername());
    }
}
