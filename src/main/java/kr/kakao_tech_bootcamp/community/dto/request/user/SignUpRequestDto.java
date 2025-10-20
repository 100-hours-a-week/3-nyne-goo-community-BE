package kr.kakao_tech_bootcamp.community.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class SignUpRequestDto {
    @Schema(example = "test@example.com")
    private String email;

    @Schema(example = "Pword1!")
    private String password;

    @Schema(example = "test")
    private String nickname;
}
