package kr.kakao_tech_bootcamp.community.exception.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCodeType{
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "JWT 토큰이 존재하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "잘못된 형식의 JWT 토큰입니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
