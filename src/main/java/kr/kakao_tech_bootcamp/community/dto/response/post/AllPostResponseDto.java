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

    public AllPostResponseDto plusCounts(AllPostResponseDto post, int likesCount, int commentsCount, int viewsCount) {
        int like = post.getLikesCount()+likesCount;
        int comments = post.getCommentsCount()+commentsCount;
        int views = post.getViewsCount()+viewsCount;

        return new AllPostResponseDto(
                post.getPostId(), post.getTitle(), post.getCreatedAt(), post.getUpdatedAt(),
                like, comments, views, post.isLike(), post.getAuthor()
        );
    }
}
