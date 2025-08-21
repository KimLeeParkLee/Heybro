package com.heybro.heybro.onboarding.controller;

import com.heybro.heybro.onboarding.dto.response.OnboardingQuestionResponse;
import com.heybro.heybro.onboarding.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
@Tag(name = "온보딩", description = "온보딩 API")
@Slf4j
public class OnboardingController {
    private final OnboardingService onboardingService;

    @Operation(summary = "온보딩 질문 및 선택지 조회")
    @GetMapping("/questions")
    public List<OnboardingQuestionResponse> getOnboardingQuestions() {
        return onboardingService.findOnboardingQuestions();
    }
}
