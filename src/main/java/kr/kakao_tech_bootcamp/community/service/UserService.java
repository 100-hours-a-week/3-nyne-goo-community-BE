package kr.kakao_tech_bootcamp.community.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.UserStatus;
import kr.kakao_tech_bootcamp.community.dto.request.ImageRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.ChangeMyInfoRequestDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

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
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        // 닉네임, 비밀번호 길이 확인
        if (signUpRequestDto.getNickname().length() > 10 || signUpRequestDto.getNickname().isEmpty()) {
            throw new RestApiException(UserErrorCode.INVALID_NICKNAME);
        }

        if (signUpRequestDto.getPassword().length() > 16 || signUpRequestDto.getPassword().length() < 8) {
            throw new RestApiException(UserErrorCode.INVALID_PASSWORD);
        }

        // 닉네임, 비밀번호 중복 확인 (curl 접근하는 경우 고려)
        if(userRepository.findByEmail(signUpRequestDto.getEmail()).isPresent() || userRepository.findByNickname(signUpRequestDto.getNickname()).isPresent()) {
            throw new RestApiException(CommonErrorCode.CONFLICT);
        }

        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
        ImageRequestDto imageRequestDto = signUpRequestDto.getImage();

        User user = new User(signUpRequestDto.getEmail(), signUpRequestDto.getNickname(), encodedPassword, imageRequestDto==null?null:imageRequestDto.getImagePath(), imageRequestDto==null?null:imageRequestDto.getImageName());
        userRepository.save(user);

        return SignUpResponseDto.of(user.getId(), user.getEmail());
    }

    // 내 정보 조회
    @Transactional(readOnly = true) // 읽기 전용. 변경 감지 x -> 불필요한 DB I/O 생략
    public GetMeResponseDto getMyInfo(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(CommonErrorCode.NOT_FOUND));
        System.out.println("User image path = " + user.getImagePath());
        return GetMeResponseDto.of(user.getImagePath(), user.getEmail(), user.getNickname());
    }

    // 회원정보 수정
    public void changeMyInfo(int userId, ChangeMyInfoRequestDto changeMyInfoRequestDto) {
        // 닉네임 길이 확인
        String nickname = changeMyInfoRequestDto.getNickname();
        ImageRequestDto imageRequestDto = changeMyInfoRequestDto.getImage();
        String imagePath = imageRequestDto.getImagePath();
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요");
        }
        if (nickname.length() > 10) {
            throw new RestApiException(UserErrorCode.INVALID_NICKNAME);
        }

        User user = userRepository.getReferenceById(userId);

        // 닉네임 중복 사전 검증 (자기 자신 제외)
        if (userRepository.existsByNickname(nickname) && !user.getNickname().equals(nickname)) {
            throw new RestApiException(CommonErrorCode.CONFLICT);
        }

        user.setNickname(nickname);

        if (imagePath != null) {
            user.setImage(imagePath, imageRequestDto.getImageName());
        }
    }

    // 현재 비밀번호 확인
    public CheckPasswordResponseDto checkPassword(int userId, CheckPasswordRequestDto checkPasswordRequestDto) {
        User user = userRepository.getReferenceById(userId);

        boolean isMatch = passwordEncoder.matches(checkPasswordRequestDto.getPassword(), user.getPassword());
        if (!isMatch) throw new RestApiException(UserErrorCode.INVALID_PASSWORD);

        return CheckPasswordResponseDto.of(isMatch);
    }

    // 비밀번호 변경
    public void changePassword(int userId, String newPassword) {
        if (newPassword.length() < 8 || newPassword.length() > 16) {
            throw new RestApiException(UserErrorCode.INVALID_PASSWORD);
        }

        User user = userRepository.getReferenceById(userId);

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        // save() 불필요 -> dirty checking 자동 처리!
    }

    // 회원 삭제
    public void delete(int userId) {
        User user = userRepository.getReferenceById(userId);

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new RestApiException(CommonErrorCode.BAD_REQUEST );
        }

        user.deleteUser();

        // DB에서 토큰 삭제
        refreshTokenRepository.deleteByUserId(userId);
    }
}
