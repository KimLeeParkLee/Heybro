package com.heybro.heybro.qna.service;

import com.heybro.heybro.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionViewService {

    private final QuestionRepository questionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementViewCount(Long questionId) {
        questionRepository.incrementViewCount(questionId);
    }
}
