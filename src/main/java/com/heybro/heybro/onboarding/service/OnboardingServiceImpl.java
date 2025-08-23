package com.heybro.heybro.onboarding.service;

import com.heybro.heybro.onboarding.domain.OnboardingOption;
import com.heybro.heybro.onboarding.domain.OnboardingQuestion;
import com.heybro.heybro.onboarding.domain.UserOnboardingAnswer;
import com.heybro.heybro.onboarding.dto.request.OnboardingResultRequestDto;
import com.heybro.heybro.onboarding.dto.response.OnboardingOptionsResponseDto;
import com.heybro.heybro.onboarding.dto.response.OnboardingQuestionResponseDto;
import com.heybro.heybro.onboarding.dto.response.UserTypeResponseDto;
import com.heybro.heybro.onboarding.repository.OnboardingOptionRepository;
import com.heybro.heybro.onboarding.repository.OnboardingQuestionRepository;
import com.heybro.heybro.onboarding.repository.UserOnboardingAnswerRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserType;
import com.heybro.heybro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingServiceImpl implements OnboardingService {
    private final OnboardingQuestionRepository onboardingQuestionRepository;
    private final OnboardingOptionRepository onboardingOptionRepository;
    private final UserOnboardingAnswerRepository userOnboardingAnswerRepository;
    private final UserRepository userRepository;


    @Override
    public List<OnboardingQuestionResponseDto> findOnboardingQuestions() {
        List<OnboardingQuestion> questions = onboardingQuestionRepository.findAll();

        return questions.stream()
                .map(question -> OnboardingQuestionResponseDto.builder()
                        .onboardingQuestionId(question.getId())
                        .questionContent(question.getContent())
                        .displayOrder(question.getDisplayOrder())
                        .options(question.getOptions().stream()
                                .map(option -> OnboardingOptionsResponseDto.builder() // options 리스트도 DTO로 변환
                                        .onboardingOptionId(option.getId())
                                        .optionContent(option.getContent())
                                        .displayOrder(option.getDisplayOrder())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UserTypeResponseDto submitResults(OnboardingResultRequestDto request, UserDetails userDetails) {
        // 1. userId로 User 엔티티를 DB에서 조회
        User user = userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 2. 요청으로 들어온 답변 DTO 리스트를 실제 Answer 엔티티 리스트로 변환
        List<UserOnboardingAnswer> answers = request.getAnswers().stream()
                .map(answerDto -> {
                    // 3. answerDto에 있는 optionId로 OnboardingOption 엔티티를 DB에서 조회
                    OnboardingQuestion question = onboardingQuestionRepository.findById(answerDto.getOnboardingQuestionId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 질문을 찾을 수 없습니다. ID: " + answerDto.getOnboardingQuestionId()));

                    // 3. answerDto에 있는 optionId로 OnboardingOption 엔티티를 DB에서 조회
                    OnboardingOption option = onboardingOptionRepository.findById(answerDto.getOnboardingOptionId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 선택지를 찾을 수 없습니다. ID: " + answerDto.getOnboardingOptionId()));

                    // 4. 조회한 엔티티들로 UserOnboardingAnswer를 생성
                    return UserOnboardingAnswer.builder()
                            .user(user)
                            .question(question) // 3번에서 조회한 Option 엔티티를 통해 Question 엔티티를 가져옴
                            .selectedOption(option) // 3번에서 조회한 Option 엔티티
                            .build();
                })
                .collect(Collectors.toList());

        // 5. 변환된 Answer 엔티티 리스트를 DB에 한번에 저장
        userOnboardingAnswerRepository.saveAll(answers);

        // 6. 저장된 답변들을 기반으로 최종 UserType을 계산
        UserType finalUserType = calculateUserType(answers);

        // 7. 조회한 User 엔티티의 타입을 업데이트
        user.updateUserType(finalUserType);

        // 8. 최종 결과를 DTO로 변환하여 반환
        return UserTypeResponseDto.from(finalUserType);
    }

    /**
     * 피부 우선순위 : 민감성 > 지성 > 복합성 > 건성
     * 민감성이나 지성 피부의 특징을 하나라도 가지고 있다면, 예민항 피부를 기준으로 케어하는 것이 가장 안전
     * 따라서 1:1:1:1이거나 2:2일 경우 우선순위에 따라 타입 배정
     * 생활 타입은 우선순위 x 동점일 경우 가장 일반적인 타입으로 결정
     */

    public UserType calculateUserType(List<UserOnboardingAnswer> answers) {
        Map<Integer, Integer> skinTypeCounts = new HashMap<>();
        Map<Integer, Integer> lifestyleCounts = new HashMap<>();

        for (UserOnboardingAnswer answer : answers) {
            OnboardingQuestion question = answer.getQuestion();
            OnboardingOption option = answer.getSelectedOption();

            int questionOrder = question.getDisplayOrder();
            int optionOrder = option.getDisplayOrder();

            if (questionOrder >= 1 && questionOrder <= 4) {
                skinTypeCounts.put(optionOrder, skinTypeCounts.getOrDefault(optionOrder, 0) + 1);
            } else if (questionOrder >= 5 && questionOrder <= 8) {
                lifestyleCounts.put(optionOrder, lifestyleCounts.getOrDefault(optionOrder, 0) + 1);
            }
        }

        String finalSkinType = determineSkinType(skinTypeCounts);
        String finalLifestyle = determineLifestyle(lifestyleCounts);

        String finalTypeName = finalSkinType + "_" + finalLifestyle;
        return UserType.valueOf(finalTypeName);
    }

    private String determineSkinType(Map<Integer, Integer> counts) {
        if (counts.isEmpty()) return "COMBINATION"; // 기본값, 혹은 예외 처리

        int maxCount = Collections.max(counts.values());

        List<Integer> candidates = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == maxCount) {
                candidates.add(entry.getKey());
            }
        }

        // 후보가 1개면 바로 반환
        if (candidates.size() == 1) {
            return convertSkinTypeToEnumName(candidates.get(0));
        }

        // 동점일 경우 우선순위에 따라 결정 (민감성 > 지성 > 복 합성 > 건성)
        if (candidates.contains(3)) return "SENSITIVE";
        if (candidates.contains(4)) return "OILY";
        if (candidates.contains(2)) return "COMBINATION";
        return "DRY"; // 기본값
    }

    private String determineLifestyle(Map<Integer, Integer> counts) {
        if (counts.isEmpty()) return "NORMAL"; // 기본값, 혹은 예외 처리

        int maxCount = Collections.max(counts.values());

        List<Integer> candidates = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == maxCount) {
                candidates.add(entry.getKey());
            }
        }

        if (candidates.size() == 1) {
            return convertLifestyleToEnumName(candidates.get(0));
        }

        // 동점일 경우 NORMAL 타입으로 결정
        return "NORMAL";
    }

    private String convertSkinTypeToEnumName(int type) {
        return switch (type) {
            case 1 -> "DRY";
            case 2 -> "COMBINATION";
            case 3 -> "SENSITIVE";
            case 4 -> "OILY";
            default -> "DRY";
        };
    }

    private String convertLifestyleToEnumName(int type) {
        return switch (type) {
            case 1 -> "OFFICE";
            case 2 -> "OUTDOOR";
            case 3 -> "FASHIONABLE";
            case 4 -> "NORMAL";
            default -> "NORMAL";
        };
    }
}
