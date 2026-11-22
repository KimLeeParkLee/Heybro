package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.Answer;
import com.heybro.heybro.qna.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllByQuestion(Question question);

    @Query("SELECT a FROM Answer a " +
            "LEFT JOIN FETCH a.user " +
            "WHERE a.question.id = :questionId")
    List<Answer> findAnswersByQuestionIdWithUser(@Param("questionId") Long questionId);
}
