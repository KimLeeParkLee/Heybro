package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom  {
    @Modifying
    @Query("update Question q set q.viewCount = q.viewCount + 1 where q.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query(value = "SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.answers",
            countQuery = "SELECT count(q) FROM Question q")
    Page<Question> findAllWithAnswers(Pageable pageable);
}
