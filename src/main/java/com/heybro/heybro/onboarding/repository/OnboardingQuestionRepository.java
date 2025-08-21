package com.heybro.heybro.onboarding.repository;

import com.heybro.heybro.onboarding.domain.OnboardingQuestion;
import com.heybro.heybro.onboarding.dto.response.OnboardingQuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingQuestionRepository extends JpaRepository<OnboardingQuestion, Long> {
}
