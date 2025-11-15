package kr.kakao_tech_bootcamp.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.entity.RefreshToken;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CookieUtil {
    private final JwtProvider jwtProvider;
    private final String ACCESS_TOKEN = "accessToken";
    private final String REFRESH_TOKEN = "refreshToken";

    // accessToken, refreshToken 쿠키 한번에 추가
    public void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        int accessMaxAge  = secondsUntil(jwtProvider.getExpirationDateFromToken(accessToken));   // exp from JWT
        int refreshMaxAge = secondsUntil(jwtProvider.getExpirationDateFromToken(refreshToken));
        addTokenCookie(response, ACCESS_TOKEN,  accessToken,  accessMaxAge);
        addTokenCookie(response, REFRESH_TOKEN, refreshToken, refreshMaxAge);
    }

    // 만료 시각에서 현재 시각을 뺀 seconds 값을 반환
    private int secondsUntil(Instant exp) {
        long sec = java.time.Duration.between(Instant.now(), exp).getSeconds();
        if (sec < 0) sec = 0; // 이미 만료된 경우
        return (sec > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) sec;
    }

    public void deleteTokenCookies(HttpServletResponse response) {
        addTokenCookie(response, ACCESS_TOKEN, "", 0);
        addTokenCookie(response, REFRESH_TOKEN, "", 0);
    }

    // 쿠키 생성
    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
