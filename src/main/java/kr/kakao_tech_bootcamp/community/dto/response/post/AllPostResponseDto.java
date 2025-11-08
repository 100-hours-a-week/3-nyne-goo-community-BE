package kr.kakao_tech_bootcamp.community.dto.response.post;

import kr.kakao_tech_bootcamp.community.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AllPostResponseDto {
    private int postId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likesCount;
    private int commentsCount;
    private int viewsCount;
    private boolean isLike;
    private Author author;

    public AllPostResponseDto plusCounts(int likesCount, int commentsCount, int viewsCount) {
        int like =this.likesCount+likesCount;
        int comments = this.commentsCount+commentsCount;
        int views = this.viewsCount+viewsCount;

        return new AllPostResponseDto(
                postId, title, createdAt, updatedAt, like, comments, views, isLike, author
        );
    }
}
