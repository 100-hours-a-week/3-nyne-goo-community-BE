package kr.kakao_tech_bootcamp.community.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    // 추후 바꿔야 하는 부분!!
    @Value("${jwt.secret}")
    private String secretKeyString;

    private Key key;

    // spring이 @Value 주입 끝낸 후 키 생성
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // access token 생성
    public String generateAccessToken(int userId, String role) {
        long accessTtlSeconds = 5*60;   // 5분

        return Jwts.builder()
                .setSubject(String.valueOf(userId))                                             // 유저 식별자
                .claim("role", role)                                                         // USER, ADMIN과 같은 사용자 권한 정보 (현재는 USER만 존재)
                .setIssuedAt(new Date())                                                        // 발급 시각
                .setExpiration(Date.from(Instant.now().plusSeconds(accessTtlSeconds)))          // 만료 시각
                .signWith(key, SignatureAlgorithm.HS256)                                        // 서명 (비밀키 기반)
                .compact();                                                                     // 문자열 형태로 반환
    }

    // 서명, 만료 검증하고 payload 반환
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    // refresh token 생성
    public String generateRefreshToken(int userId) {
        long refreshTtlSeconds = 7L*24*3600;    // 7일;
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshTtlSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 userId 추출
    public int getIdFromToken(String token) {
        var jws = parse(token);
        return Integer.parseInt(jws.getBody().getSubject());
    }
}
