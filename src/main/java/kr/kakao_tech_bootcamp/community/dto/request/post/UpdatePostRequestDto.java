package kr.kakao_tech_bootcamp.community.dto.request.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePostRequestDto {
    private String title;
    private String content;
}
