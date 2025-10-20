package kr.kakao_tech_bootcamp.community.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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


}
