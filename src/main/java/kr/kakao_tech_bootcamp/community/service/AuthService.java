package kr.kakao_tech_bootcamp.community.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.UserStatus;
import kr.kakao_tech_bootcamp.community.dto.TokenResponseDto;
import kr.kakao_tech_bootcamp.community.dto.request.user.LoginRequestDto;
import kr.kakao_tech_bootcamp.community.entity.RefreshToken;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.exception.error_code.UserErrorCode;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.repository.RefreshTokenRepository;
import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    public TokenResponseDto login(LoginRequestDto request) {
        // 이메일 확인
        User user = userRepository.findByActiveEmail(request.getEmail())
                .orElseThrow(() -> new RestApiException(UserErrorCode.INVALID_CREDENTIALS));

        boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println(isMatch);

        // 비밀번호 확인
        if (!isMatch) {
            throw new RestApiException(UserErrorCode.INVALID_CREDENTIALS);
        }

        // 기존 refresh token 무효화
        refreshTokenRepository.deleteByUserId(user.getId());
        String accessToken = jwtProvider.generateAccessToken(user.getId(), "USER");
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        // refresh token 저장
        RefreshToken refreshEntity = RefreshToken.of(user.getId(), refreshToken, jwtProvider.getExpirationDateFromToken(refreshToken));
        refreshTokenRepository.save(refreshEntity);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    // 로그아웃
    public void logout(int userId) {
        // DB에서 userId 에 해당하는 refreshToken 모두 삭제
        refreshTokenRepository.deleteByUserId(userId);
    }

    // 토큰 재발급
    @Transactional
    public TokenResponseDto tokenReissue(String refreshToken) {
        if(refreshToken==null){
            throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        }

        var parsedRefreshToken = jwtProvider.parse(refreshToken);

        // 저장된 refreshToken 찾음
        RefreshToken entity = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()->new RestApiException(CommonErrorCode.UNAUTHORIZED));

        // 이미 만료됐으면 unauthorized 에러
        if(entity.getExpiresAt().isBefore(Instant.now())) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);

        int userId = Integer.parseInt(parsedRefreshToken.getBody().getSubject());
        refreshTokenRepository.deleteByUserId(userId);      // 유저가 발급받은 refreshToken 삭제

        // access, refresh token 갱신
        String newAccessToken = jwtProvider.generateAccessToken(userId, "USER");
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        // 갱신한 refresh token 저장
        RefreshToken newRefreshEntity = RefreshToken.of(userId, newRefreshToken, jwtProvider.getExpirationDateFromToken(newRefreshToken));
        refreshTokenRepository.save(newRefreshEntity);

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }
}