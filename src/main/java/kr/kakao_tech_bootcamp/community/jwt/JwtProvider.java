package kr.kakao_tech_bootcamp.community.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.kakao_tech_bootcamp.community.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    // 추후 바꿔야 하는 부분!!
    @Value("${jwt.secret}")
    private String secretKeyString;

    private Key key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // 30분짜리 Access Token
    private final long accessTokenValidity = 1000L * 60 * 30;

    // 토큰 생성
    public String generateAccessToken(int userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))             // 유저 식별자
                .setIssuedAt(now)               // 발급 시각
                .setExpiration(expiry)          // 만료 시각
                .signWith(key)                  // 서명 (비밀키 기반)
                .compact();                     // 문자열 형태로 반환
    }

    // 만료 여부 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true; // 유효한 토큰
        } catch (Exception e) {
            return false; // 서명 위조, 만료 등
        }
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        // 헤더에서 토큰 찾기
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // 쿠키에서 토큰 찾기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 토큰에서 이메일 뽑기
    public Integer getIdFromToken(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("JWT 토큰이 존재하지 않습니다.");
        }

        try {
            return Integer.parseInt(Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new UnauthorizedException("JWT 토큰이 만료되었습니다.");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new UnauthorizedException("잘못된 형식의 JWT 토큰입니다.");
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 JWT 토큰입니다.");
        }
    }
}
