package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.AnswerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerImageRepository extends JpaRepository<AnswerImage, Long> {
}
