package com.heybro.heybro.onboarding.service;

import com.heybro.heybro.onboarding.dto.request.OnboardingResultRequestDto;
import com.heybro.heybro.onboarding.dto.response.OnboardingQuestionResponseDto;
import com.heybro.heybro.user.dto.response.UserTypeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OnboardingService {
    List<OnboardingQuestionResponseDto> findOnboardingQuestions();

    UserTypeResponseDto submitResults(OnboardingResultRequestDto request, String email);
}
