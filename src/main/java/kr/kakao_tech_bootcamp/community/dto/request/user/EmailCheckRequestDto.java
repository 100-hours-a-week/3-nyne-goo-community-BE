package kr.kakao_tech_bootcamp.community.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailCheckRequestDto {
    private String email;
}
