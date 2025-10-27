package kr.kakao_tech_bootcamp.community.manager;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.entity.User;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private static final String SESSION_COOKIE_NAME = "SID";
    private Map<String, User> sessionStore = new ConcurrentHashMap<>();

    // 세션 생성
    public void createSession(User value, HttpServletResponse response) {
        // UUID 랜덤으로 생성해서 sessionId 에 넣고 user와 함께 sessionStore에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        ResponseCookie sessionCookie = ResponseCookie.from(SESSION_COOKIE_NAME, sessionId)
                .httpOnly(true)       // JS에서 접근 불가 → XSS 방어
                .secure(false)        // HTTPS 환경이라면 true로 (로컬 개발은 false)
                .sameSite("Lax")      // 크롬 CSRF 기본 방어 (또는 Strict)
                .path("/")            // 모든 경로에서 접근 가능
                .maxAge(60 * 30)      // 30분 (JWT 만료와 맞추기)
                .build();

        // 헤더에 쿠키 추가
        response.addHeader("Set-Cookie", sessionCookie.toString());
    }

    // 세션으로부터 유저 가져오기
    public User getSession(HttpServletRequest request) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);
        if (cookie == null) return null;

        return sessionStore.get(cookie.getValue());
    }

    // 세션 만료시키기
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);
        if (cookie != null) {
            sessionStore.remove(cookie.getValue());

            // 클라이언트 쿠키도 삭제
            ResponseCookie expiredCookie = ResponseCookie.from(SESSION_COOKIE_NAME, "")
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(0)
                    .build();
            response.addHeader("Set-Cookie", expiredCookie.toString());
        }
    }

    // 쿠키 찾기
    private Cookie findCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
