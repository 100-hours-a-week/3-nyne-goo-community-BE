package kr.kakao_tech_bootcamp.community.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.user.ChangePasswordRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.CheckPasswordRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.CheckPasswordResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.GetMeResponseDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.SessionManager;
import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import kr.kakao_tech_bootcamp.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserInfoController {
    private final UserService userService;
    private final SessionManager sessionManager;

    @GetMapping
    @Operation(summary = "회원정보 조회")
    public ResponseEntity<ApiResponse<GetMeResponseDto>> getMe(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "회원 정보를 성공적으로 조회했습니다.", userService.getMyInfo(sessionManager.getSession(request, response))));
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "내 정보 변경")
    public ResponseEntity<ApiResponse<Void>> changeMyInfo(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String nickname,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        userService.changeMyInfo(sessionManager.getSession(request, response), nickname, image);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "회원 정보가 수정되었습니다."));
    }

    @PostMapping(path = "/password")
    @Operation(summary = "비밀번호 확인", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<CheckPasswordResponseDto>> checkPassword(HttpServletRequest request, HttpServletResponse response, @RequestBody CheckPasswordRequestDto checkPasswordRequestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "비밀번호가 일치합니다.", userService.checkPassword(sessionManager.getSession(request, response), checkPasswordRequestDto)));
    }

    @PatchMapping(path = "/password")
    @Operation(summary = "비밀번호 변경", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Void>> changePassword(HttpServletRequest request, HttpServletResponse response, @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        userService.changePassword(sessionManager.getSession(request, response), changePasswordRequestDto.getPassword());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "비밀번호를 변경했습니다."));
    }
}
