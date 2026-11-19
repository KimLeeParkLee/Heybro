package com.heybro.heybro.qna.service;

import com.heybro.heybro.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 질문 조회수 증가를 담당하는 별도 빈.
 * 호출자(QnaServiceImpl.getQuestion)가 트랜잭션 없이 호출하므로 일반 @Transactional(REQUIRED)이면 충분하다.
 * 예전에는 바깥 읽기 트랜잭션 안에서 REQUIRES_NEW로 실행해 요청당 커넥션을 2개 점유했고,
 * 동시 요청이 풀 크기를 넘으면 안쪽 트랜잭션이 커넥션을 얻지 못해 풀이 고갈됐다.
 */
@Service
@RequiredArgsConstructor
public class QuestionViewService {

    private final QuestionRepository questionRepository;

    @Transactional
    public void incrementViewCount(Long questionId) {
        questionRepository.incrementViewCount(questionId);
    }
}
