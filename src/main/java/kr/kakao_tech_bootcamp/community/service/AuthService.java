package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.dto.request.user.LoginRequestDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.UnauthorizedException;
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
    public String login(LoginRequestDto request) {
        User user = userRepository.findByActiveEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 이메일입니다."));

        System.out.println("user: " + user);
        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        return jwtProvider.generateAccessToken(user.getId());
    }
}