package com.heybro.heybro.qna.controller;

import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import com.heybro.heybro.qna.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
@Tag(name = "Q&A", description = "Q&A API")
@Slf4j
public class QnaController {
    private final QnaService qnaService;

//    @Operation(summary = "질문 목록 조회")
//    @GetMapping
//    public Page<QuestionListResponseDto> getQuestions(@RequestParam(required = false) String sort,
//                                                      @RequestParam(required = false) Long categoryId,
//                                                      @RequestParam(defaultValue = "1") int page,
//                                                      @RequestParam(defaultValue = "10") int size,
//                                                      @RequestParam(required = false) String search) {
//        return qnaService.getQuestions(sort, categoryId, page, size, search);
//    }

    @Operation(summary = "질문 상세 조회")
    @GetMapping("/{question_id}")
    public QuestionResponseDto getQuestion(@PathVariable Long question_id) {
        return qnaService.getQuestion(question_id);
    }
}
