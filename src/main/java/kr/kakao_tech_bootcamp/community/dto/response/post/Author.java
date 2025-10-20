package kr.kakao_tech_bootcamp.community.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    private String profileImageUrl;
    private String name;
    private boolean mine;
}
