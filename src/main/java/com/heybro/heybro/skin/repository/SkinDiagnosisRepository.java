package com.heybro.heybro.skin.repository;

import com.heybro.heybro.skin.domain.SkinDiagnosis;
import com.heybro.heybro.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkinDiagnosisRepository extends JpaRepository<SkinDiagnosis, Long> {
    Optional<SkinDiagnosis> findFirstByUserOrderByDiagnosisDateAsc(User user);

    Optional<SkinDiagnosis> findFirstByUserOrderByDiagnosisDateDesc(User user);
}
