package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.QuestionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionImageRepository extends JpaRepository<QuestionImage, Long> {
}
