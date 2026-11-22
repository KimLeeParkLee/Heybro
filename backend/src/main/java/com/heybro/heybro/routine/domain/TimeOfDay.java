package com.heybro.heybro.routine.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TimeOfDay {
    MORNING("아침용"),
    LUNCH("점심용"),
    EVENING("저녁용");

    private final String description;
}
