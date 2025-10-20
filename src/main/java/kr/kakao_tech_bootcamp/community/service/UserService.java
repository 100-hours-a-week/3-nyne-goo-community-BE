package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.UserStatus;
import kr.kakao_tech_bootcamp.community.dto.request.user.SignUpRequestDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.UnauthorizedException;
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
    public Boolean existEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    public Boolean existNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    public User signUp(SignUpRequestDto signUpRequestDto, MultipartFile image) {
        String imageUUID = null;
        String imageName = null;

        if(image!=null){
            imageName = image.getOriginalFilename();
            String ext = imageName.substring(imageName.lastIndexOf("."));
            imageUUID = UUID.randomUUID() + ext;

            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            try{
                Files.createDirectories(Paths.get(uploadDir));          // 저장할 폴더 준비
                Path path = Paths.get(uploadDir + imageUUID);      // 파일 저장 위치 (전체 경로)
                image.transferTo(path.toFile());                        // 파일 저장
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패",e);
            }
        }

        User user = new User(signUpRequestDto, imageUUID, imageName);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true) // 읽기 전용. 변경 감지 x -> 불필요한 DB I/O 생략
    public User getMyInfo(String token){
        int userId = jwtProvider.getIdFromToken(token);
        return userRepository.findById(userId).orElseThrow(()->new UnauthorizedException("존재하지 않는 사용자입니다."));
    }

    public void changePassword(String token, String newPassword){
        int userId = jwtProvider.getIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(()->new UnauthorizedException("존재하지 않는 사용자입니다."));

        user.setPassword(newPassword);

        // save() 불필요 -> dirty checking 자동 처리!
    }

    public void changeMyInfo(String token, String nickname, MultipartFile image){
        int userId = jwtProvider.getIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(()->new UnauthorizedException("존재하지 않는 사용자입니다."));

        user.setNickname(nickname);

        if(image!=null){
            String imageName = image.getOriginalFilename();
            String ext = imageName.substring(imageName.lastIndexOf("."));
            String imageUUID = UUID.randomUUID() + ext;

            user.setImageUUID(imageUUID);
            user.setImageName(imageName);

            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            try{
                Files.createDirectories(Paths.get(uploadDir));          // 저장할 폴더 준비
                Path path = Paths.get(uploadDir + imageUUID);      // 파일 저장 위치 (전체 경로)
                image.transferTo(path.toFile());                        // 파일 저장
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패",e);
            }
        }
    }

    public void delete(String token){
        int userId = jwtProvider.getIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(()->new UnauthorizedException("존재하지 않는 사용자입니다."));

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new IllegalStateException("이미 탈퇴한 사용자입니다.");
        }

        userRepository.delete(user);
    }
}
