package com.heybro.heybro.skin.repository;

import com.heybro.heybro.skin.domain.SkinDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinDiagnosisRepository extends JpaRepository<SkinDiagnosis, Long> {
}
