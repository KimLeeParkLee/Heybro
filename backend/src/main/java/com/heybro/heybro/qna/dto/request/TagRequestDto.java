package com.heybro.heybro.qna.dto.request;

import com.heybro.heybro.qna.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDto {
    private String content;

    public static TagRequestDto from(Tag tag) {
        return TagRequestDto.builder().content(tag.getContent()).build();
    }
}