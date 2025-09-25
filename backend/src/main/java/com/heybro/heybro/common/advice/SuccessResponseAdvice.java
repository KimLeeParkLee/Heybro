package com.heybro.heybro.common.advice;

import com.heybro.heybro.common.advice.NoApiResponse;
import com.heybro.heybro.common.response.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.heybro.heybro")
public class SuccessResponseAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 이 Advice를 적용할지 여부를 결정하는 메서드
     * @return true를 반환하면 beforeBodyWrite 메서드가 실행
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // @NoApiResponse 어노테이션이 클래스나 메서드에 붙어있으면 Advice 적용 제외
        if (returnType.getContainingClass().isAnnotationPresent(NoApiResponse.class) ||
            returnType.hasMethodAnnotation(NoApiResponse.class)) {
            return false;
        }

        // 이미 ApiResponse 형태로 감싸져 있다면 중복으로 감싸지 않도록 false를 반환
        if (returnType.getParameterType().isAssignableFrom(ApiResponse.class)) {
            return false;
        }

        return true;
    }

    /**
     * 컨트롤러에서 반환된 원본 body를 가공하는 메서드
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 반환된 body가 없는 경우(void 등) 성공 응답 객체를 생성
        if (body == null) {
            return ApiResponse.success();
        }

        // 반환된 body가 있는 경우, 성공 응답 객체로 감싸줌
        return ApiResponse.success(body);
    }
}