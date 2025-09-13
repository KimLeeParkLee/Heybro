package com.heybro.heybro.common.advice;

import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FailResponseAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("handleIllegalArgumentException: {}", e.getMessage());
        return ApiResponse.error(e.getMessage(), 400);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("handleResourceNotFoundException: {}", e.getMessage());
        return ApiResponse.error(e.getMessage(), 404);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        return ApiResponse.error("서버에 예기치 않은 오류가 발생했습니다.", 500);
    }
}