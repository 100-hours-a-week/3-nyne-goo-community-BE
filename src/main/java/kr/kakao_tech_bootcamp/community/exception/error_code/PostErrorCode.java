package kr.kakao_tech_bootcamp.community.exception.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCodeType{
    INVALID_TITLE(HttpStatus.BAD_REQUEST, "제목은 1자 이상 26자 이하로 작성해주세요."),
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "내용은 1자 이상 2000자 이하로 작성해주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}
