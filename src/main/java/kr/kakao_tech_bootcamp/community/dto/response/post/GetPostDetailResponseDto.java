package kr.kakao_tech_bootcamp.community.dto.response.post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetPostDetailResponseDto extends AllPostResponseDto {
    private String content;
    private List<ImageResponseDto> imageList;

    public GetPostDetailResponseDto(
            int postId,
            String title,
            String content,
            LocalDateTime createdDate,
            LocalDateTime updatedDate,
            int likesCount,
            int commentsCount,
            int viewsCount,
            boolean isLike,
            Author author
    ) {
        super(postId, title, createdDate, updatedDate, likesCount, commentsCount, viewsCount, isLike, author);
        this.content = content;
        this.imageList = new ArrayList<>();
    }
}
