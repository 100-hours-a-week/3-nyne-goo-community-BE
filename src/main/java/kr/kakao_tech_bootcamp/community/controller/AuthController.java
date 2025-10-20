package kr.kakao_tech_bootcamp.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.user.LoginRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.LoginResponseDto;
import kr.kakao_tech_bootcamp.community.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "로그인")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto request) {
        String accessToken = authService.login(request);
        System.out.println("accessToken: " + accessToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "로그인 성공입니다.", LoginResponseDto.of(accessToken)));
    }

    // 로그아웃은 클라이언트에서 엑세스토큰 삭제하는 것으로 함
}