package kr.kakao_tech_bootcamp.community.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")      // boolean 값 가져와서 dto 만듦
public class ExistCheckResponseDto {
    private Boolean exist;
}
