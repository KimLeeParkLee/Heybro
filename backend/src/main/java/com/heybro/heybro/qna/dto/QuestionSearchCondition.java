package com.heybro.heybro.qna.dto;

import com.heybro.heybro.qna.domain.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionSearchCondition {
    private QuestionCategory category;
    private String search;
}
