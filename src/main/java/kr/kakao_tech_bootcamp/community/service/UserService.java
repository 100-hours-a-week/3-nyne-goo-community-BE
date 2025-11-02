package kr.kakao_tech_bootcamp.community.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.UserStatus;
import kr.kakao_tech_bootcamp.community.dto.request.user.CheckPasswordRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.SignUpRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.CheckPasswordResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.GetMeResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.SignUpResponseDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.exception.error_code.UserErrorCode;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.repository.RefreshTokenRepository;
import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final AuthHelper authHelper;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 이메일 중복 확인
    @Transactional(readOnly = true)
    public Boolean existEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // 닉네임 중복 확인
    @Transactional(readOnly = true)
    public Boolean existNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    // 회원가입
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto, MultipartFile image) {
        // 닉네임, 비밀번호 길이 확인
        if (signUpRequestDto.getNickname().length() > 10 || signUpRequestDto.getNickname().isEmpty()) {
            throw new RestApiException(UserErrorCode.INVALID_NICKNAME);
        }

        if (signUpRequestDto.getPassword().length() > 16 || signUpRequestDto.getPassword().length() < 8) {
            throw new RestApiException(UserErrorCode.INVALID_PASSWORD);
        }

        String imageUUID = null;
        String imageName = null;

        if (image != null) {
            // 이미지 파일에서 확장자 추출해서 랜덤 UUID값에 확장자 붙여서 저장
            imageName = image.getOriginalFilename();
            String ext = imageName.substring(imageName.lastIndexOf("."));
            imageUUID = UUID.randomUUID() + ext;

            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            try {
                Files.createDirectories(Paths.get(uploadDir));          // 저장할 폴더 준비
                Path path = Paths.get(uploadDir + imageUUID);      // 파일 저장 위치 (전체 경로)
                image.transferTo(path.toFile());                        // 파일 저장
            } catch (IOException e) {
                throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        User user = new User(signUpRequestDto.getEmail(), signUpRequestDto.getNickname(), signUpRequestDto.getPassword(), imageUUID, imageName);
        return SignUpResponseDto.from(userRepository.save(user));
    }

    // 내 정보 조회
    @Transactional(readOnly = true) // 읽기 전용. 변경 감지 x -> 불필요한 DB I/O 생략
    public GetMeResponseDto getMyInfo(HttpServletRequest request) {
        User user = authHelper.findUserFromRequest(request);
        return GetMeResponseDto.from(user);
    }

    // 회원정보 수정
    public void changeMyInfo(HttpServletRequest request, String nickname, MultipartFile image) {
        // 닉네임 길이 확인
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요");
        }
        if (nickname.length() > 10) {
            throw new RestApiException(UserErrorCode.INVALID_NICKNAME);
        }

        User user = authHelper.findUserFromRequest(request);

        // 닉네임 중복 사전 검증 (자기 자신 제외)
        if (userRepository.existsByNickname(nickname) && !user.getNickname().equals(nickname)) {
            throw new RestApiException(CommonErrorCode.CONFLICT);
        }

        user.setNickname(nickname);

        if (image != null) {
            String imageName = image.getOriginalFilename();
            String ext = imageName.substring(imageName.lastIndexOf("."));
            String imageUUID = UUID.randomUUID() + ext;

            user.setImage(imageUUID, imageName);
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            try {
                Files.createDirectories(Paths.get(uploadDir));          // 저장할 폴더 준비
                Path path = Paths.get(uploadDir + imageUUID);      // 파일 저장 위치 (전체 경로)
                image.transferTo(path.toFile());                        // 파일 저장
            } catch (IOException e) {
                throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
    }

    // 현재 비밀번호 확인
    public CheckPasswordResponseDto checkPassword(HttpServletRequest request, CheckPasswordRequestDto checkPasswordRequestDto) {
        User user = authHelper.findUserFromRequest(request);

        boolean isMatch = user.getPassword().equals(checkPasswordRequestDto.getPassword());
        if (!isMatch) throw new RestApiException(UserErrorCode.INVALID_PASSWORD);

        return CheckPasswordResponseDto.from(isMatch);
    }

    // 비밀번호 변경
    public void changePassword(HttpServletRequest request, String newPassword) {
        if (newPassword.length() < 8 || newPassword.length() > 16) {
            throw new RestApiException(UserErrorCode.INVALID_PASSWORD);
        }

        User user = authHelper.findUserFromRequest(request);

        user.setPassword(newPassword);

        // save() 불필요 -> dirty checking 자동 처리!
    }

    // 회원 삭제
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        User user = authHelper.findUserFromRequest(request);

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new RestApiException(CommonErrorCode.BAD_REQUEST );
        }

        user.deleteUser();

        // DB에서 토큰 삭제
        refreshTokenRepository.deleteByUserId(authHelper.findUserFromRequest(request).getId());
        // 쿠키의 토큰 만료시킴
        authHelper.addTokenCookie(response, "accessToken", null, 0);
        authHelper.addTokenCookie(response, "refreshToken", null, 0);
    }
}
