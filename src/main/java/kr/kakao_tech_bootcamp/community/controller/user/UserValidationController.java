package kr.kakao_tech_bootcamp.community.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.user.EmailCheckRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.ExistCheckResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserValidationController {
    private final UserService userService;

    @PostMapping("/availability")
    @Operation(summary = "이메일 중복 확인")
    public ResponseEntity<ApiResponse<ExistCheckResponseDto>> checkAvailability(@RequestBody EmailCheckRequestDto emailCheckRequestDto) {
        boolean existEmail = userService.existEmail(emailCheckRequestDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "이메일 중복확인에 성공했습니다.", ExistCheckResponseDto.of(existEmail)));
    }

    @GetMapping("/availability")
    @Operation(summary = "닉네임 중복 확인")
    public ResponseEntity<ApiResponse<ExistCheckResponseDto>> checkAvailability(@RequestParam("nickname") String nickname) {
        boolean existNickname = userService.existNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "닉네임 중복확인에 성공했습니다.", ExistCheckResponseDto.of(existNickname)));
    }
}
