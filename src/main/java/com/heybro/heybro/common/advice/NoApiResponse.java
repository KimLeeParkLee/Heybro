package com.heybro.heybro.common.advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 이 어노테이션이 적용된 컨트롤러나 메서드는
 * SuccessResponseAdvice의 API 응답 래핑(Wrapping)에서 제외됩니다.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoApiResponse {
}
