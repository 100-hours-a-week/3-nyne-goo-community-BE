package kr.kakao_tech_bootcamp.community.exception.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCodeType {
    EMAIL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
    EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임을 입력해주세요."),
    TOO_LONG_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임은 1자 이상 10자 이하로 작성해야 합니다."),
    EMPTY_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요."),
    TOO_LONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상 16자 이하로 작성해야 합니다."),
    ALREADY_DELETED(HttpStatus.CONFLICT, "이미 탈퇴한 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
