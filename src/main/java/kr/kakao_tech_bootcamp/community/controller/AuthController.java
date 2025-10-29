package kr.kakao_tech_bootcamp.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.user.LoginRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.LoginResponseDto;
import kr.kakao_tech_bootcamp.community.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "로그인")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(HttpServletResponse response, @RequestBody LoginRequestDto loginRequestDto) {
        authService.login(response, loginRequestDto);

        return ResponseEntity.ok()
                .body(ApiResponse.success(200, "로그인 성공입니다.", null));
    }

    @DeleteMapping
    @Operation(summary = "로그아웃")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);

        return ResponseEntity.ok()
                .body(ApiResponse.success(200, "로그아웃 성공", null));
    }

    @PostMapping("/refresh")
    @Operation(summary="토큰 재발급")
    public ResponseEntity<ApiResponse<Void>> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        authService.tokenReissue(refreshToken, response);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "토큰을 재발급했습니다."));
    }
}