package kr.kakao_tech_bootcamp.community.dto.response.comment;

import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.Author;
import kr.kakao_tech_bootcamp.community.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AllCommentResponseDto extends ChangeCommentResponseDto {
    private LocalDateTime updatedAt;
    private Author author;

    public AllCommentResponseDto(int commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt, Author author) {
        super(commentId, content,  createdAt);
        this.updatedAt = updatedAt;
        this.author = author;
    }
}
