package kr.kakao_tech_bootcamp.community.dto.response.comment;

import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.Author;
import kr.kakao_tech_bootcamp.community.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AllCommentResponseDto extends ChangeCommentResponseDto {
    private LocalDateTime createdAt;
    private Author author;

    public AllCommentResponseDto(int commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt, Author author) {
        super(commentId, content, updatedAt);
        this.createdAt = createdAt;
        this.author = author;
    }
}
