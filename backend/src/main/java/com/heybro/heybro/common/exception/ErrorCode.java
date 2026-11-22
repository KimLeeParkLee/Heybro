package com.heybro.heybro.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 예외의 상태 코드와 메시지를 한곳에서 관리한다.
 * 서비스는 상황에 맞는 ErrorCode 로 BusinessException 을 던지고, FailResponseAdvice 가 상태 코드로 변환한다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "포인트는 1 이상이어야 합니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    ROUTINE_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "오늘 날짜의 루틴 기록이 없습니다."),
    COUPON_CODE_GENERATION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "쿠폰 코드 생성에 실패했습니다. 잠시 후 다시 시도해 주세요.");

    private final HttpStatus status;
    private final String message;
}
