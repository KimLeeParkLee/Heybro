package com.heybro.heybro.onboarding.repository;

import com.heybro.heybro.onboarding.domain.OnboardingOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardingOptionRepository extends JpaRepository<OnboardingOption, Long> {
}
