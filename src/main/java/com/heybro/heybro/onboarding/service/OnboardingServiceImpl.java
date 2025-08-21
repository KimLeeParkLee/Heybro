package com.heybro.heybro.onboarding.service;

import com.heybro.heybro.onboarding.domain.OnboardingQuestion;
import com.heybro.heybro.onboarding.dto.response.OnboardingOptionsResponse;
import com.heybro.heybro.onboarding.dto.response.OnboardingQuestionResponse;
import com.heybro.heybro.onboarding.repository.OnboardingQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingServiceImpl implements OnboardingService {
    private final OnboardingQuestionRepository onboardingQuestionRepository;

    @Override
    public List<OnboardingQuestionResponse> findOnboardingQuestions() {
        List<OnboardingQuestion> questions = onboardingQuestionRepository.findAll();

        return questions.stream()
                .map(question -> OnboardingQuestionResponse.builder()
                        .onboardingQuestionId(question.getId())
                        .questionContent(question.getContent())
                        .displayOrder(question.getDisplayOrder())
                        .options(question.getOptions().stream()
                                .map(option -> OnboardingOptionsResponse.builder() // options 리스트도 DTO로 변환
                                        .onboardingOptionId(option.getId())
                                        .optionContent(option.getContent())
                                        .displayOrder(option.getDisplayOrder())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
