package kr.kakao_tech_bootcamp.community.dto;

import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.exception.error_code.ErrorCodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private int statusCode;
    private String code;
    private String message;
    private T data;

    // 성공 (data 있음)
    public static <T> ApiResponse<T> success(int statusCode, String message, T data) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    // 성공 (data 없음)
    public static <T> ApiResponse<T> success(int statusCode, String message) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .code("SUCCESS")
                .message(message)
                .build();
    }

    // 실패
    public static <T> ApiResponse<T> fail(ErrorCodeType errorCodeType) {
        return ApiResponse.<T>builder()
                .statusCode(errorCodeType.getHttpStatus().value())
                .code(errorCodeType.getCode())
                .message(errorCodeType.getMessage())
                .build();
    }
}
