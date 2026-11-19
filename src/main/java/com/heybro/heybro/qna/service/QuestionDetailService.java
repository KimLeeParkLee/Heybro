package com.heybro.heybro.qna.service;

import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.qna.domain.Answer;
import com.heybro.heybro.qna.domain.Question;
import com.heybro.heybro.qna.dto.response.QuestionResponseDto;
import com.heybro.heybro.qna.repository.AnswerRepository;
import com.heybro.heybro.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 질문 상세 조회(읽기 전용 트랜잭션)를 담당하는 별도 빈.
 * 조회와 DTO 변환을 모두 이 트랜잭션 안에서 끝내므로 지연 로딩 컬렉션이 OSIV에 의존하지 않는다.
 * QnaServiceImpl 안에서 자기 호출(self-invocation)하면 프록시가 우회되어 트랜잭션이 적용되지 않으므로 별도 빈으로 분리했다.
 */
@Service
@RequiredArgsConstructor
public class QuestionDetailService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestionDetail(Long questionId) {
        // 1. 질문과 관련된 대부분의 정보를 Fetch Join으로 한 번에 조회
        Question question = questionRepository.findByIdWithDetails(questionId).orElseThrow(
                () -> new ResourceNotFoundException("해당 아이디를 가진 질문을 찾을 수 없습니다: " + questionId)
        );

        // 2. 답변과 답변 작성자 정보를 Fetch Join으로 한 번에 조회
        List<Answer> answers = answerRepository.findAnswersByQuestionIdWithUser(questionId);

        // 3. DTO 변환 (지연 로딩 컬렉션 초기화까지 트랜잭션 안에서 완료)
        return QuestionResponseDto.from(question, answers);
    }
}
