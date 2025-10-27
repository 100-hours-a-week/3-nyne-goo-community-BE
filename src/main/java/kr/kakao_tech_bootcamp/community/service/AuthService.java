package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.dto.request.user.LoginRequestDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.exception.error_code.UserErrorCode;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public User login(LoginRequestDto request) {
        User user = userRepository.findByActiveEmail(request.getEmail())
                .orElseThrow(() -> new RestApiException(UserErrorCode.INVALID_CREDENTIALS));

        System.out.println("user: " + user);
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RestApiException(UserErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }
}