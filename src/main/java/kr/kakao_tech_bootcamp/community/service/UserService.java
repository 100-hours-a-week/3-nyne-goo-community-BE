package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.UserStatus;
import kr.kakao_tech_bootcamp.community.dto.SessionUserDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.CheckPasswordRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.SignUpRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.CheckPasswordResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.ExistCheckResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.GetMeResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.SignUpResponseDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.exception.error_code.UserErrorCode;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
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
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ExistCheckResponseDto existEmail(String email) {
        Boolean isExistEmail = userRepository.findByEmail(email).isPresent();
        return ExistCheckResponseDto.of(isExistEmail);
    }

    @Transactional(readOnly = true)
    public ExistCheckResponseDto existNickname(String nickname) {
        Boolean isExistNickname = userRepository.findByNickname(nickname).isPresent();
        return ExistCheckResponseDto.of(isExistNickname);
    }

    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto, MultipartFile image) {
        String imageUUID = null;
        String imageName = null;

        if (image != null) {
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

        if (signUpRequestDto.getNickname().length() > 10 || signUpRequestDto.getNickname().length() == 0) {
            throw new RestApiException(UserErrorCode.TOO_LONG_NICKNAME);
        }

        if (signUpRequestDto.getPassword().length() > 16 || signUpRequestDto.getPassword().length() < 8) {
            throw new RestApiException(UserErrorCode.TOO_LONG_PASSWORD);
        }

        User user = new User(signUpRequestDto, imageUUID, imageName);
        return SignUpResponseDto.from(userRepository.save(user));
    }

    @Transactional(readOnly = true) // 읽기 전용. 변경 감지 x -> 불필요한 DB I/O 생략
    public GetMeResponseDto getMyInfo(SessionUserDto sessionUserDto) {
        if(sessionUserDto == null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        return GetMeResponseDto.from(user);
    }

    public void changeMyInfo(SessionUserDto sessionUserDto, String nickname, MultipartFile image) {
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());

        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요");
        }

        if (nickname.length() > 10) {
            throw new RestApiException(UserErrorCode.TOO_LONG_NICKNAME);
        }

        // 닉네임 중복 사전 검증 (자기 자신 제외)
        if (userRepository.existsByNickname(nickname) && !user.getNickname().equals(nickname)) {
            throw new RestApiException(CommonErrorCode.CONFLICT);
        }

        if (image != null) {
            String imageName = image.getOriginalFilename();
            String ext = imageName.substring(imageName.lastIndexOf("."));
            String imageUUID = UUID.randomUUID() + ext;

            user.setImageUUID(imageUUID);
            user.setImageName(imageName);

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

    public CheckPasswordResponseDto checkPassword(SessionUserDto sessionUserDto, CheckPasswordRequestDto checkPasswordRequestDto) {
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        boolean isMatch = user.getPassword().equals(checkPasswordRequestDto.getPassword());
        if (!isMatch) throw new RestApiException(UserErrorCode.INVALID_PASSWORD);

        return CheckPasswordResponseDto.from(isMatch);
    }

    public void changePassword(SessionUserDto sessionUserDto, String newPassword) {
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        if (newPassword.length() < 8 || newPassword.length() > 16) {
            throw new RestApiException(UserErrorCode.TOO_LONG_PASSWORD);
        }

        user.setPassword(newPassword);

        // save() 불필요 -> dirty checking 자동 처리!
    }

    public void delete(SessionUserDto sessionUserDto) {
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new RestApiException(UserErrorCode.ALREADY_DELETED);
        }

        userRepository.delete(user);
    }
}
