package kr.kakao_tech_bootcamp.community.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class CheckPasswordResponseDto {
    public boolean isMatch;
}
