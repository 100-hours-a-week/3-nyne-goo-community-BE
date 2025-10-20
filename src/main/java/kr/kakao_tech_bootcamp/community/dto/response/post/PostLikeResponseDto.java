package kr.kakao_tech_bootcamp.community.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostLikeResponseDto {
    private int postId;
    private int likesCount;

    public static PostLikeResponseDto of(int postId, int likesCount) {
        return new PostLikeResponseDto(postId, likesCount);
    }
}
