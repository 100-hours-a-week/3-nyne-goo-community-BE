package kr.kakao_tech_bootcamp.community.dto.response.user;

import kr.kakao_tech_bootcamp.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponseDto {
    private int userId;

    public static SignUpResponseDto from(User user) {
        return new SignUpResponseDto(user.getId());
    }
}
