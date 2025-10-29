package kr.kakao_tech_bootcamp.community.exception.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCodeType {
    INVALID_COMMENT(HttpStatus.BAD_REQUEST, "댓글은 1자 이상 500자 이하로 작성해주세요."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
