package kr.kakao_tech_bootcamp.community.dto.response.comment;

import kr.kakao_tech_bootcamp.community.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CreateCommentResponseDto {
    private int commentId;
    private String content;
    private LocalDateTime createdAt;

    public static CreateCommentResponseDto of(int commentId, String content, LocalDateTime createdAt) {
        return new CreateCommentResponseDto(commentId, content, createdAt);
    }
}
