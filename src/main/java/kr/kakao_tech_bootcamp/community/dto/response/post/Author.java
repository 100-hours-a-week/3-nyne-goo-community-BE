package kr.kakao_tech_bootcamp.community.dto.response.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class Author {
    @JsonIgnore // response 에서 숨김
    private String imageUUID;
    private String name;
    private boolean mine;

    @JsonProperty("profileImageUrl")    // response에 이 키로만 노출
    public String getProfileImageUrl(){
        return (imageUUID==null)?null:"/uploads/"+imageUUID;
    }
}
