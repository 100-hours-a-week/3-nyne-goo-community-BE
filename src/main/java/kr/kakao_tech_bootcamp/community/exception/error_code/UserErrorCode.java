package kr.kakao_tech_bootcamp.community.exception.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCodeType {
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임은 1자 이상 10자 이하로 작성해야 합니다."),
    INVALID_PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상 16자 이하로 작성해야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
