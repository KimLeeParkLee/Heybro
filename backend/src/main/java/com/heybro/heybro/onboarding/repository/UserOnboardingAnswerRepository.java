package com.heybro.heybro.onboarding.repository;

import com.heybro.heybro.onboarding.domain.UserOnboardingAnswer;
import com.heybro.heybro.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOnboardingAnswerRepository extends JpaRepository<UserOnboardingAnswer, Long> {
    void deleteAllByUser(User user);

    @Modifying
    @Query("delete from UserOnboardingAnswer a where a.user = :user")
    int deleteBulkByUser(@Param("user") User user);
}
