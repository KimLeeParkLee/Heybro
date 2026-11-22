package com.heybro.heybro.qna;

import com.heybro.heybro.qna.domain.Answer;
import com.heybro.heybro.qna.domain.AnswerImage;
import com.heybro.heybro.qna.domain.Question;
import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import com.heybro.heybro.qna.repository.AnswerRepository;
import com.heybro.heybro.qna.repository.QuestionRepository;
import com.heybro.heybro.qna.service.QnaService;
import com.heybro.heybro.support.IntegrationTestSupport;
import com.heybro.heybro.user.domain.User;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionDetailQueryCountTest extends IntegrationTestSupport {

    @Autowired QnaService qnaService;
    @Autowired QuestionRepository questionRepository;
    @Autowired AnswerRepository answerRepository;
    @Autowired EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("답변 10개(이미지 2장씩)인 질문 상세 조회는 답변 수와 무관하게 6개의 SQL 만 실행한다")
    void getQuestion_queryCountDoesNotGrowWithAnswers() {
        User writer = saveUser("qna-writer@test.local", 0);
        Question question = questionRepository.save(Question.builder()
                .title("제목").content("내용").createdAt(LocalDateTime.now()).user(writer).build());
        for (int i = 0; i < 10; i++) {
            User answerer = saveUser("qna-answerer" + i + "@test.local", 0);
            Answer answer = Answer.builder().content("답변 " + i).createdAt(LocalDateTime.now()).user(answerer).question(question).build();
            answer.addAnswerImage(AnswerImage.builder().answerImage("a").sortOrder(1).build());
            answer.addAnswerImage(AnswerImage.builder().answerImage("b").sortOrder(2).build());
            answerRepository.save(answer);
        }

        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear();
        QuestionResponseDto dto = qnaService.getQuestion(question.getId());

        // 조회수 UPDATE 1, 질문+작성자+질문이미지 1, 답변+작성자 1, 태그 1, 답변 이미지(IN) 1, 카테고리 1
        assertThat(dto.getAnswers()).hasSize(10);
        assertThat(dto.getAnswers().get(0).getAnswerImages()).hasSize(2);
        assertThat(stats.getPrepareStatementCount()).isEqualTo(6);
    }
}
