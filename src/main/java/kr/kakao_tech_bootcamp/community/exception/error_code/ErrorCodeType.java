package kr.kakao_tech_bootcamp.community.exception.error_code;

import org.springframework.http.HttpStatus;

public interface ErrorCodeType {
    HttpStatus getHttpStatus();
    String getMessage();

    default String getCode(){
        return ((Enum<?>) this).name();
    }
}
