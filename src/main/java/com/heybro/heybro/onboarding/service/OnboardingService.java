package com.heybro.heybro.onboarding.service;

import com.heybro.heybro.onboarding.dto.response.OnboardingQuestionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OnboardingService {
    List<OnboardingQuestionResponse> findOnboardingQuestions();
}
