package kr.kakao_tech_bootcamp.community.service;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.UserStatus;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final AuthHelper authHelper;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인
    @Transactional
    public void login(HttpServletResponse response, LoginRequestDto request) {
        // 이메일 확인
        User user = userRepository.findByActiveEmail(request.getEmail())
                .orElseThrow(() -> new RestApiException(UserErrorCode.INVALID_CREDENTIALS));

        // 비밀번호 확인
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RestApiException(UserErrorCode.INVALID_CREDENTIALS);
        }

        // 기존 refresh token 무효화
        refreshTokenRepository.deleteByUserId(user.getId());

        // 새 토큰 발급 및 저장
        var tokenResponse = authHelper.generateAndSaveTokens(user);

        // 쿠키 추가
        authHelper.addTokenCookies(response, tokenResponse);
    }

    // 로그아웃
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // DB에서 userId 에 해당하는 refreshToken 모두 삭제
        refreshTokenRepository.deleteByUserId(authHelper.findUserFromRequest(request).getId());
        authHelper.addTokenCookie(response, "accessToken", null, 0);
        authHelper.addTokenCookie(response, "refreshToken", null, 0);
    }

    // 토큰 재발급
    @Transactional
    public void tokenReissue(String refreshToken, HttpServletResponse response) {
        if(refreshToken==null){
            throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        }

        var parsedRefreshToken = jwtProvider.parse(refreshToken);

        // 저장된 refreshToken 찾아서 entity에 저장
        RefreshToken entity = refreshTokenRepository.findByRefreshTokenAndRevokedFalse(refreshToken).orElseThrow(()->new RestApiException(CommonErrorCode.UNAUTHORIZED));

        if(entity.getExpiresAt().isBefore(Instant.now())) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);

        int userId = Integer.parseInt(parsedRefreshToken.getBody().getSubject());
        User user = userRepository.findById(userId).orElseThrow(()->new RestApiException(CommonErrorCode.UNAUTHORIZED));

        // access, refresh token 갱신
        var tokenResponse = authHelper.generateAndSaveTokens(user);

        // 쿠키 추가
        authHelper.addTokenCookies(response, tokenResponse);

    }
}