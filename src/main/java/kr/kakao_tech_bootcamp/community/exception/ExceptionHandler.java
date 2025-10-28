package kr.kakao_tech_bootcamp.community.exception;

import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.exception.error_code.ErrorCodeType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(RestApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleRestApiException(RestApiException e) {
        ErrorCodeType code = e.getErrorCode();
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.fail(e.getErrorCode()));
    }
}
