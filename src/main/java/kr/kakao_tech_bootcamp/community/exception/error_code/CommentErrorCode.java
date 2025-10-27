package kr.kakao_tech_bootcamp.community.exception.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCodeType {
    EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "댓글을 작성해주세요."),
    TOO_LONG_CONTENT(HttpStatus.BAD_REQUEST, "댓글은 500자 이하로 작성해주세요."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않습니다."),
    FORBIDDEN_COMMENT(HttpStatus.FORBIDDEN, "본인 댓글만 수정 및 삭제할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
