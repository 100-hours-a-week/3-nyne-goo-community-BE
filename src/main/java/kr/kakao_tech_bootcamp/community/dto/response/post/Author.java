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
    private ImageResponseDto image;
    private String name;
    private boolean mine;
}
