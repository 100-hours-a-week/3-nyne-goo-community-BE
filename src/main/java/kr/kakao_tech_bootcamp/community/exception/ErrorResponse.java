package kr.kakao_tech_bootcamp.community.exception;

import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final String code;      // ex: USER_NOT_FOUND
    private final String message;   // ex: User not found
    private final int status;       //ex: 404

    public static ErrorResponse from(CommonErrorCode commonErrorCode) {
        return ErrorResponse.builder()
                .code(commonErrorCode.name())
                .message(commonErrorCode.getMessage())
                .status(commonErrorCode.getHttpStatus().value())
                .build();
    }
}
