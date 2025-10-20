package kr.kakao_tech_bootcamp.community.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameCheckRequestDto {
    private String nickname;
}
