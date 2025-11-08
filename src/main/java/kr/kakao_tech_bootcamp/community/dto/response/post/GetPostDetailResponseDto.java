package kr.kakao_tech_bootcamp.community.dto.response.post;

import kr.kakao_tech_bootcamp.community.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GetPostDetailResponseDto extends AllPostResponseDto {
    private String content;
    private List<ImageResponseDto> imageList;

    public GetPostDetailResponseDto(
            int postId,
            String title,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            int likesCount,
            int commentsCount,
            int viewsCount,
            boolean isLike,
            Author author,
            List<ImageResponseDto> imageList
    ) {
        super(postId, title, createdAt, updatedAt, likesCount, commentsCount, viewsCount, isLike, author);
        this.content = content;
        this.imageList = (imageList == null) ? new ArrayList<>() : new ArrayList<>(imageList);
    }

    public GetPostDetailResponseDto withImages(List<ImageResponseDto> imageList) {
        return new GetPostDetailResponseDto(
                getPostId(), getTitle(), getContent(),
                getCreatedAt(), getUpdatedAt(),
                getLikesCount(), getCommentsCount(), getViewsCount(),
                isLike(), getAuthor(),
                imageList
        );
    }

    public GetPostDetailResponseDto plusCounts( int like, int comment, int view) {
        int likesCount = getLikesCount() + like;
        int commentsCount = getCommentsCount() + comment;
        int viewsCount = getViewsCount() + view;

        return new GetPostDetailResponseDto(
                getPostId(), getTitle(), getContent(), getCreatedAt(), getUpdatedAt(),
                likesCount, commentsCount, viewsCount, isLike(), getAuthor(), getImageList()
        );
    }
}
