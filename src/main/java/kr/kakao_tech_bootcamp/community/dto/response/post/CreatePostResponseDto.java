package kr.kakao_tech_bootcamp.community.dto.response.post;

import kr.kakao_tech_bootcamp.community.entity.Post;
import kr.kakao_tech_bootcamp.community.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CreatePostResponseDto {
    private int postId;
    private String title;
    private String content;
    private List<String> imageList;

    public static CreatePostResponseDto from(Post post) {
        List<String> images = new ArrayList<>();
        for(PostImage postImage:post.getImages()){
            images.add(postImage.getImageName());
        }

        return CreatePostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageList(images)
                .build();
    }
}
