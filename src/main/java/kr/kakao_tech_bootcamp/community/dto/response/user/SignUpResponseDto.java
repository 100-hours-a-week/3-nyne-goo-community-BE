package kr.kakao_tech_bootcamp.community.dto.response.user;

import kr.kakao_tech_bootcamp.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor (staticName = "of")
public class SignUpResponseDto {
    private int userId;
    private String email;
}
