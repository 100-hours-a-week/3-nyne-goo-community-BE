package kr.kakao_tech_bootcamp.community.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.user.EmailCheckRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.SignUpRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.ExistCheckResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.SignUpResponseDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.SessionManager;
import kr.kakao_tech_bootcamp.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManageController {
    private final UserService userService;
    private final SessionManager sessionManager;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원가입")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(
            @Valid @ModelAttribute SignUpRequestDto signUpRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "회원가입 되었습니다.", userService.signUp(signUpRequestDto, image)));
    }

    @DeleteMapping
    @Operation(summary = "회원탈퇴", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Void>> deleteUser(HttpServletRequest request, HttpServletResponse response) {
        userService.delete(sessionManager.getSession(request, response));
        return ResponseEntity.ok(ApiResponse.success(200, "회원탈퇴에 성공했습니다."));
    }

    @PostMapping("/availability")
    @Operation(summary = "이메일 중복 확인")
    public ResponseEntity<ApiResponse<ExistCheckResponseDto>> checkAvailability(@RequestBody EmailCheckRequestDto emailCheckRequestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "이메일 중복확인에 성공했습니다.", userService.existEmail(emailCheckRequestDto.getEmail())));
    }

    @GetMapping("/availability")
    @Operation(summary = "닉네임 중복 확인")
    public ResponseEntity<ApiResponse<ExistCheckResponseDto>> checkAvailability(@RequestParam("nickname") String nickname) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "닉네임 중복확인에 성공했습니다.", userService.existNickname(nickname)));
    }
}
