package kr.kakao_tech_bootcamp.community.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    // 필터 제외 경로 목록 (로그인, 회원가입, 약관 동의)
    private static final String[] EXCLUDED_PATHS = {
            "/login", "/signup", "/terms", "/error"
    };

    // 필터 제외 경로 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.stream(EXCLUDED_PATHS).anyMatch(path::startsWith);
    }

    // 실제 필터링 로직
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws IOException, ServletException {
        boolean isHome = isHomeRequest(request);
        Optional<String> token = extractToken(request);

        // 토큰 없으면 home 요청 시 login 으로 리다이렉트
        if (token.isEmpty()) {
            if (isHome) {
                response.sendRedirect("/login");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // 토큰 검증 및 속성 설정
        if (!validateAndSetAttributes(token.get(), request)) {
            // 토큰 잘못된 경우 -> 401 에러
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }

    // home 요청인지 확인
    private boolean isHomeRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "/".equals(uri) || "/home".equals(uri);
    }

    // 토큰 추출 (헤더 우선, 쿠키 다음)
    private Optional<String> extractToken(HttpServletRequest request) {
        return extractTokenFromHeader(request)
                .or(() -> extractTokenFromCookie(request));
    }

    // 헤더에서 토큰 추출
    private Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }

    // 쿠키에서 토큰 추출
    private Optional<String> extractTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    // 토큰 검증 및 요청 속성 설정
    private boolean validateAndSetAttributes(String token, HttpServletRequest request) {
        try {
            var jws = jwtProvider.parse(token);
            Claims body = jws.getBody();
            request.setAttribute("userId", Integer.valueOf(body.getSubject()));
            request.setAttribute("role", body.get("role"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
