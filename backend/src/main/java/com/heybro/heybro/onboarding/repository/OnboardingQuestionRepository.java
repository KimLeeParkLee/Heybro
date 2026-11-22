package com.heybro.heybro.onboarding.repository;

import com.heybro.heybro.onboarding.domain.OnboardingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingQuestionRepository extends JpaRepository<OnboardingQuestion, Long> {
    @Query("select q from OnboardingQuestion q left join fetch q.options")
    List<OnboardingQuestion> findAllWithDetails();
}
