package kr.kakao_tech_bootcamp.community.dto.request.post;

import kr.kakao_tech_bootcamp.community.dto.request.ImageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequestDto {
    private String title;
    private String content;
    private List<ImageRequestDto> imageList;
}
