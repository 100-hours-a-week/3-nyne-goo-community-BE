package kr.kakao_tech_bootcamp.community.exception.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCodeType{
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    ALREADY_DELETED(HttpStatus.CONFLICT, "이미 삭제된 게시글입니다."),
    EMPTY_TITLE(HttpStatus.BAD_REQUEST, "제목을 입력해주세요."),
    EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "내용을 입력해주세요."),
    TOO_LONG_TITLE(HttpStatus.BAD_REQUEST, "제목은 26자 이하로 작성해주세요."),
    TOO_LONG_CONTENT(HttpStatus.BAD_REQUEST, "내용은 2000자 이하로 작성해주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}
