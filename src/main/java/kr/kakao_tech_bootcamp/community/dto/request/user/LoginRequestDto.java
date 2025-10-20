package kr.kakao_tech_bootcamp.community.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequestDto {
    // 스웨거에서 바로 로그인하기 위해 붙임
    @Schema(example = "nyne@kakao.kr")
    private String email;

    @Schema(example = "Pword1!!")
    private String password;
}
