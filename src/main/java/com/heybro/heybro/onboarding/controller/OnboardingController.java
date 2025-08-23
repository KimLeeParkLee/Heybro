package com.heybro.heybro.onboarding.controller;

import com.heybro.heybro.onboarding.dto.request.OnboardingResultRequestDto;
import com.heybro.heybro.onboarding.dto.response.OnboardingQuestionResponseDto;
import com.heybro.heybro.user.dto.response.UserTypeResponseDto;
import com.heybro.heybro.onboarding.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public List<OnboardingQuestionResponseDto> getOnboardingQuestions() {
        return onboardingService.findOnboardingQuestions();
    }

    @Operation(summary = "온보딩 결과 전송")
    @PostMapping("/results")
    public UserTypeResponseDto submitOnboardingResults(@RequestBody OnboardingResultRequestDto request, @AuthenticationPrincipal UserDetails userDetails) {
        return onboardingService.submitResults(request, userDetails);
    }
}
