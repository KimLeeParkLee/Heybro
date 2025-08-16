package com.heybro.heybro.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
// JsonInclude.Include.NON_NULL: null인 필드는 JSON으로 변환할 때 무시
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final int code;

    // 생성자를 private으로 막아서 정적 팩토리 메서드만 사용하도록 유도
    private ApiResponse(boolean success, T data, String message, int code) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    // 성공 응답을 위한 정적 팩토리 메서드 (데이터 포함)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "성공적으로 처리되었습니다.", 200);
    }

    // 성공 응답을 위한 정적 팩토리 메서드 (데이터 미포함)
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, "성공적으로 처리되었습니다.", 200);
    }

    // 실패 응답을 위한 정적 팩토리 메서드
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(false, null, message, code);
    }
}