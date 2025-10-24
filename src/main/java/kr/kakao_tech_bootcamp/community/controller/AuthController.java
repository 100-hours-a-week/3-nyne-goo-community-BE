package kr.kakao_tech_bootcamp.community.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto request) {
        String accessToken = authService.login(request);
        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)       // JS에서 접근 불가 → XSS 방어
                .secure(false)        // HTTPS 환경이라면 true로 (로컬 개발은 false)
                .sameSite("Lax")      // 크롬 CSRF 기본 방어 (또는 Strict)
                .path("/")            // 모든 경로에서 접근 가능
                .maxAge(60 * 30)      // 30분 (JWT 만료와 맞추기)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // 쿠키 전달
                .body(ApiResponse.success(200, "로그인 성공입니다.", null));
    }

    @DeleteMapping
    @Operation(summary = "로그아웃")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0) // 즉시 만료
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(ApiResponse.success(200, "로그아웃 성공", null));
    }
}