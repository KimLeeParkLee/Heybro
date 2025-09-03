package com.heybro.heybro.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserType {
    DRY_OFFICE("건성-오피스형"),
    DRY_OUTDOOR("건성-아웃도어형"),
    DRY_FASHIONABLE("건성-패셔너블형"),
    DRY_NORMAL("건성-노멀형"),

    COMBINATION_OFFICE("복합성-오피스형"),
    COMBINATION_OUTDOOR("복합성-아웃도어형"),
    COMBINATION_FASHIONABLE("복합성-패셔너블형"),
    COMBINATION_NORMAL("복합성-노멀형"),

    SENSITIVE_OFFICE("민감성-오피스형"),
    SENSITIVE_OUTDOOR("민감성-아웃도어형"),
    SENSITIVE_FASHIONABLE("민감성-패셔너블형"),
    SENSITIVE_NORMAL("민감성-노멀형"),

    OILY_OFFICE("지성-오피스형"),
    OILY_OUTDOOR("지성-아웃도어형"),
    OILY_FASHIONABLE("지성-패셔너블형"),
    OILY_NORMAL("지성-노멀형");

    private final String description;
}