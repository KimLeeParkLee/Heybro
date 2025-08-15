package com.heybro.heybro.common.advice;

import com.heybro.heybro.common.exception.ResourceNotFoundException;
import com.heybro.heybro.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FailResponseAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.error(e.getMessage(), 400);
    }

//    @ExceptionHandler(ResourceNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ApiResponse<?> handleResourceNotFoundException(ResourceNotFoundException e) {
//        return ApiResponse.error(e.getMessage(), 404);
//    }

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ApiResponse<?> handleException(Exception e) {
//        // Log the exception for debugging purposes
//        // log.error("Unhandled exception", e);
//        return ApiResponse.error("서버 내부 오류가 발생했습니다.", 500);
//    }
}