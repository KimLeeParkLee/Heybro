package com.heybro.heybro.onboarding.repository;

import com.heybro.heybro.onboarding.domain.OnboardingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardingQuestionRepository extends JpaRepository<OnboardingQuestion, Long> {

}
