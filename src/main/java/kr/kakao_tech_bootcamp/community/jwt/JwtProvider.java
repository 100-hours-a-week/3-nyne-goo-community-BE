package kr.kakao_tech_bootcamp.community.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.*;

@Component
public class JwtProvider {
    // 추후 바꿔야 하는 부분!!
    @Value("${jwt.secret}")
    private String secretKeyString;
    private Key key;
    private static final int ACCESS_TOKEN_EXPIRATION = 5 * 60; // 5분
    private static final int REFRESH_TOKEN_EXPIRATION = 7 * 24 * 3600; // 7일
    // spring이 @Value 주입 끝낸 후 키 생성
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // access token 생성
    public String generateAccessToken(int userId, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))                                             // 유저 식별자
                .claim("role", role)                                                         // USER, ADMIN과 같은 사용자 권한 정보 (현재는 USER만 존재)
                .setIssuedAt(new Date())                                                        // 발급 시각
                .setExpiration(Date.from(Instant.now().plusSeconds(ACCESS_TOKEN_EXPIRATION)))          // 만료 시각
                .signWith(key, SignatureAlgorithm.HS256)                                        // 서명 (비밀키 기반)
                .compact();                                                                     // 문자열 형태로 반환
    }

    // 서명, 만료 검증하고 payload 반환
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    // refresh token 생성
    public String generateRefreshToken(int userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // request 에서 userid 추출
    public int extractUserIdFromRequest(HttpServletRequest request) {
        String accessToken = Arrays.stream(
                        Optional.ofNullable(request.getCookies()).orElse(new Cookie[0])
                )
                .filter(c -> "accessToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RestApiException(CommonErrorCode.UNAUTHORIZED));

        var jws = parse(accessToken);
        return Integer.parseInt(jws.getBody().getSubject());
    }

    // 토큰으로부터 만료 시각 추출
    public Instant getExpirationDateFromToken(String token) {
        Jws<Claims> jws = parse(token);
        return jws.getBody().getExpiration().toInstant();
    }
}
