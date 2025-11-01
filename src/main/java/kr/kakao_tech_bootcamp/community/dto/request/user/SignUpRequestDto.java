package kr.kakao_tech_bootcamp.community.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다.")
    @Schema(example = "test@example.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Schema(example = "Pword1!")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(max = 10, message = "닉네임은 10자를 초과할 수 없습니다.")
    @Schema(example = "test")
    private String nickname;
}
