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

    public static CreateCommentResponseDto from(Comment comment) {
        return new CreateCommentResponseDto(comment.getId(), comment.getContent(), comment.getCreatedAt());
    }
}
