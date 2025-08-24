package com.heybro.heybro.point.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TransactionType {
    EARN("적립"),
    USE("사용");

    private final String description;
}