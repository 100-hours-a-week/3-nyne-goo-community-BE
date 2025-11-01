package kr.kakao_tech_bootcamp.community.repository;

import kr.kakao_tech_bootcamp.community.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);  // token 값과 일치하며 아직 무효화되지 않은 refreshTokens
    void deleteByUserId(int userId);    // userId 에 연결된 모든 리프레시 토큰 일괄 삭제


    @Modifying
    @Query("delete from RefreshToken r where r.expiresAt < :now")
    void deleteExpiredRefreshTokens(@Param("now") Instant now);

}
