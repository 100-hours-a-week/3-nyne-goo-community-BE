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
    @JsonIgnore // response 에서 숨김
    private String imageUUID;
    private String email;
    private String nickname;

    @JsonProperty("profileImageUrl")    // response에 이 키로만 노출
    public String getProfileImageUrl(){
        return (imageUUID==null)?null:imageUUID;
    }
}
