// src/shared/types/api.d.ts

/**
 * 모든 API 응답에 대한 표준 래퍼(wrapper) 인터페이스입니다.
 * @template T 응답의 `data` 필드에 해당하는 타입입니다.
 */
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  code: number;
}
