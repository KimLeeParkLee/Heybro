package com.heybro.heybro.skin.repository;

import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.skin.domain.SkinDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkinDiagnosisRepository extends JpaRepository<SkinDiagnosis, Long> {
    Optional<SkinDiagnosis> findFirstByUserOrderByDiagnosisDateAsc(User user);

    Optional<SkinDiagnosis> findFirstByUserOrderByDiagnosisDateDesc(User user);

    @Modifying
    @Query("delete from SkinDiagnosis s where s.user = :user")
    int deleteBulkByUser(@Param("user") User user);
}
