package kr.kakao_tech_bootcamp.community.dto.response.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.kakao_tech_bootcamp.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class GetMeResponseDto {
    private String imagePath;
    private String email;
    private String nickname;
}
