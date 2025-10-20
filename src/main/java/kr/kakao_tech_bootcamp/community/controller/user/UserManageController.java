package kr.kakao_tech_bootcamp.community.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.user.SignUpRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.SignUpResponseDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManageController {
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원가입")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(
            @ModelAttribute SignUpRequestDto signUpRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        User user = userService.signUp(signUpRequestDto, image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "회원가입 되었습니다.", SignUpResponseDto.from(user)));
    }

    @DeleteMapping
    @Operation(summary = "회원탈퇴", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Void>> deleteUser(HttpServletRequest request){
        userService.delete(jwtProvider.getTokenFromRequest(request));
        return ResponseEntity.ok(ApiResponse.success(200, "회원탈퇴에 성공했습니다."));
    }
}
