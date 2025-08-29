package com.heybro.heybro.qna.repository;

import com.heybro.heybro.qna.domain.Answer;
import com.heybro.heybro.qna.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllByQuestion(Question question);
}
