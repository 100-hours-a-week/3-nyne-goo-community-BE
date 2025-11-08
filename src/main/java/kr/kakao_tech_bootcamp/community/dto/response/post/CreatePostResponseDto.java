package kr.kakao_tech_bootcamp.community.dto.response.post;

import kr.kakao_tech_bootcamp.community.entity.Post;
import kr.kakao_tech_bootcamp.community.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CreatePostResponseDto {
    private int postId;
    private String title;
    private String content;
    private List<String> imageList;

    public static CreatePostResponseDto of(int postId, String title, String content, List<PostImage> postImages) {
        List<String> images = new ArrayList<>();
        for(PostImage postImage:postImages) {
            images.add(postImage.getImageName());
        }

        return new CreatePostResponseDto(postId, title, content, images);
    }
}
