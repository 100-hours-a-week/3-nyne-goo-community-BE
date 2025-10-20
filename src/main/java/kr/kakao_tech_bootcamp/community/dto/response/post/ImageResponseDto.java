package kr.kakao_tech_bootcamp.community.dto.response.post;

import kr.kakao_tech_bootcamp.community.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {
    private String ImageName;
    private String ImageUrl;

    public static ImageResponseDto from(PostImage postImage) {
        return new ImageResponseDto(postImage.getImageName(), "http://localhost:8080/uploads/"+postImage.getImageUUID());
    }
}
