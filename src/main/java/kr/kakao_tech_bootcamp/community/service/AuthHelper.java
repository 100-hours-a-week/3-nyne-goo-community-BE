package kr.kakao_tech_bootcamp.community.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.entity.RefreshToken;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.repository.RefreshTokenRepository;
import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthHelper {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final int ACCESS_TOKEN_EXPIRATION = 5 * 60; // 5분
    private static final int REFRESH_TOKEN_EXPIRATION = 7 * 24 * 3600; // 7일

    // accessToken, refreshToken 새로 발급
    protected TokenResponse generateAndSaveTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getNickname());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        // refresh token 저장
        RefreshToken refreshEntity = RefreshToken.of(user.getId(), refreshToken, Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION), false);
        refreshTokenRepository.save(refreshEntity);

        return new TokenResponse(accessToken, refreshToken);
    }


    // accessToken, refreshToken 쿠키 한번에 추가
    protected void addTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        addTokenCookie(response, "accessToken", tokenResponse.accessToken(), ACCESS_TOKEN_EXPIRATION);
        addTokenCookie(response, "refreshToken", tokenResponse.refreshToken(), REFRESH_TOKEN_EXPIRATION);
    }

    // 쿠키 생성
    protected void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // request로 user 찾기
    protected User findUserFromRequest(HttpServletRequest request) {
        int userId = extractUserIdFromRequest(request);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.UNAUTHORIZED));
    }

    // request 에서 userid 추출
    private int extractUserIdFromRequest(HttpServletRequest request) {
        String accessToken = Arrays.stream(
                        Optional.ofNullable(request.getCookies()).orElse(new Cookie[0])
                )
                .filter(c -> "accessToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RestApiException(CommonErrorCode.UNAUTHORIZED));

        return jwtProvider.getIdFromToken(accessToken);
    }

    public record TokenResponse(String accessToken, String refreshToken) {
    }
}
