package kr.kakao_tech_bootcamp.community.dto.request.user;

import kr.kakao_tech_bootcamp.community.dto.request.ImageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeMyInfoRequestDto {
    private String nickname;
    private ImageRequestDto image;
}
