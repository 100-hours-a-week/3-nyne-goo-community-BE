package kr.kakao_tech_bootcamp.community.exception;

import kr.kakao_tech_bootcamp.community.exception.error_code.ErrorCodeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {
    private final ErrorCodeType errorCode;
}
