package kr.kakao_tech_bootcamp.community.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequestDto {
    private String imagePath;
    private String imageName;
}
