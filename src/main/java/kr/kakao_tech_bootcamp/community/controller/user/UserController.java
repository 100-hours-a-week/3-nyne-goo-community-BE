package kr.kakao_tech_bootcamp.community.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.user.ChangePasswordRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.CheckPasswordRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.EmailCheckRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.SignUpRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.CheckPasswordResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.ExistCheckResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.GetMeResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.SignUpResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.service.UserService;
import kr.kakao_tech_bootcamp.community.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;

    @PostMapping
    @Operation(summary = "회원가입")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(
            @RequestBody SignUpRequestDto signUpRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "회원가입 되었습니다.", userService.signUp(signUpRequestDto)));
    }

    @DeleteMapping
    @Operation(summary = "회원탈퇴", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Void>> deleteUser(HttpServletRequest request, HttpServletResponse response) {
        int userId = jwtProvider.extractUserIdFromRequest(request);
        userService.delete(userId);
        cookieUtil.deleteTokenCookies(response);        // 쿠키에서 토큰 삭제
        return ResponseEntity.ok(ApiResponse.success(200, "회원탈퇴에 성공했습니다."));
    }

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

    @GetMapping
    @Operation(summary = "회원정보 조회")
    public ResponseEntity<ApiResponse<GetMeResponseDto>> getMe(HttpServletRequest request) {
        int userId = jwtProvider.extractUserIdFromRequest(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "회원 정보를 성공적으로 조회했습니다.", userService.getMyInfo(userId)));
    }

    @PatchMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "내 정보 변경", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Void>> changeMyInfo(
            HttpServletRequest request,
            @RequestParam String nickname,
            @RequestPart(value = "image", required = false) MultipartFile image){
        int userId = jwtProvider.extractUserIdFromRequest(request);
        userService.changeMyInfo(userId, nickname, image);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "회원 정보가 수정되었습니다."));
    }

    @PostMapping(path = "/password")
    @Operation(summary = "비밀번호 확인", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<CheckPasswordResponseDto>>  checkPassword(HttpServletRequest request, @RequestBody CheckPasswordRequestDto checkPasswordRequestDto) {
        int userId = jwtProvider.extractUserIdFromRequest(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "비밀번호가 일치합니다.", userService.checkPassword(userId,checkPasswordRequestDto)));
    }

    @PatchMapping(path = "/password")
    @Operation(summary = "비밀번호 변경", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Void>>  changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        int userId = jwtProvider.extractUserIdFromRequest(request);
        userService.changePassword(userId, changePasswordRequestDto.getPassword());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "비밀번호를 변경했습니다."));
    }
}
