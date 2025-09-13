package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom  {
    @Modifying
    @Query("update Question q set q.viewCount = q.viewCount + 1 where q.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query("SELECT q FROM Question q " +
            "LEFT JOIN FETCH q.user " +
            "LEFT JOIN FETCH q.questionImages " +
            "WHERE q.id = :questionId")
    Optional<Question> findByIdWithDetails(@Param("questionId") Long questionId);
}
