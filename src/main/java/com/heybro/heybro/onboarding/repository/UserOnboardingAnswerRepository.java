package com.heybro.heybro.onboarding.repository;

import com.heybro.heybro.onboarding.domain.UserOnboardingAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOnboardingAnswerRepository extends JpaRepository<UserOnboardingAnswer, Long> {
}
