package com.heybro.heybro.qna.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CustomPageResponse<T> {
    private List<T> content;
    private boolean last;
    private int pageSize;
    private int pageNumber;
}
