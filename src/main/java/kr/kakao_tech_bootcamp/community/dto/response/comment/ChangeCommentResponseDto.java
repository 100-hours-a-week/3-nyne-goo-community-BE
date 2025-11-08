package kr.kakao_tech_bootcamp.community.dto.response.comment;

import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCommentResponseDto {
    private int commentId;
    private String content;
    private LocalDateTime updatedAt;

    // of 사용해서 dto 만듦
    // 만들 때마다 new 사용하지 않아도 됨
    public static ChangeCommentResponseDto of(int commentId, String content, LocalDateTime updatedAt) {
        return new ChangeCommentResponseDto(
                commentId, content, updatedAt
        );
    }
}
