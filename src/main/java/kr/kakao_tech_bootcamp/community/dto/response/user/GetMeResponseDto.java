package kr.kakao_tech_bootcamp.community.dto.response.user;

import kr.kakao_tech_bootcamp.community.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMeResponseDto {
    private String profileImgUrl;
    private String email;
    private String nickname;

    public static GetMeResponseDto from(User user) {
        String imageUrl = null;
        if(user.getImageUUID()!=null) imageUrl = "http://localhost:8080/uploads/" + user.getImageUUID();
        return GetMeResponseDto.builder()
                .profileImgUrl(imageUrl)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
