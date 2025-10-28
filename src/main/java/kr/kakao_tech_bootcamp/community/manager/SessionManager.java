package kr.kakao_tech_bootcamp.community.manager;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.dto.SessionUserDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import org.springframework.http.ResponseCookie;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private static final String SESSION_COOKIE_NAME = "SID";
    private static final int SESSION_DURATION = 60 * 30;     // 30분
    private final Map<String, SessionUserDto> sessionStore = new ConcurrentHashMap<>();

    // 세션 생성
    public void createSession(User user, HttpServletResponse response) {
        // UUID 랜덤으로 생성해서 sessionId 에 넣고 user와 함께 sessionStore에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, SessionUserDto.of(user.getId(), SESSION_DURATION));
        System.out.println("sessiontStore: " + sessionStore);

        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        sessionCookie.setHttpOnly(true);       // JS에서 접근 불가 → XSS 방어
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(SESSION_DURATION);
        response.addCookie(sessionCookie);

    }

    // 세션으로부터 유저 가져오기
    public SessionUserDto getSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = findCookie(request);
        if (cookie == null) {
            System.out.println("쿠키 없음");
            return null;
        }

        SessionUserDto sessionUserDto = sessionStore.get(cookie.getValue());
        System.out.println("cookie: " + cookie.getValue());
        if (sessionUserDto == null) {
            System.out.println("세션 저장 안됨");
            return null;
        }
        if (sessionUserDto.isExpired()) {
            sessionStore.remove(cookie.getValue());
            System.out.println("세션 만료됨");
            return null;
        }

        // 세션 저장소에 만료 시간 갱신
        sessionUserDto.renew(SESSION_DURATION);

        // 쿠키에도 만료 시간 갱신
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, cookie.getValue());
        sessionCookie.setHttpOnly(true);       // JS에서 접근 불가 → XSS 방어
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(SESSION_DURATION);
        response.addCookie(sessionCookie);

        System.out.println("session duration: " + sessionUserDto.getExpiresAt());

        return sessionUserDto;
    }

    // 세션 만료시키기
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = findCookie(request);
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
    private Cookie findCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(SessionManager.SESSION_COOKIE_NAME))
                .findFirst()
                .orElse(null);
    }

    // 5분마다 만료된 세션 제거
    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void cleanExpiredSessions() {
        sessionStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
        System.out.println("Clean Expired Sessions" + new Date(System.currentTimeMillis()));
    }
}
