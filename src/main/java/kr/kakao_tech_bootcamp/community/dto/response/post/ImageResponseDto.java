package kr.kakao_tech_bootcamp.community.dto.response.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.kakao_tech_bootcamp.community.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ImageResponseDto {
    @JsonIgnore // response 에서 숨김
    private String imageUUID;
    private String imageName;

    @JsonProperty("imageUrl")    // response에 이 키로만 노출
    public String getProfileImageUrl(){
        return (imageUUID==null)?null:"/uploads/"+imageUUID;
    }
}
