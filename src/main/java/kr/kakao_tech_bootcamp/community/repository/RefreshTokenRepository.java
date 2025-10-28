package kr.kakao_tech_bootcamp.community.repository;

import kr.kakao_tech_bootcamp.community.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByRefreshTokenAndRevokedFalse(String refreshToken);  // token 값과 일치하며 아직 무효화되지 않은 refreshTokens
    void deleteByUserId(int userId);    // userId 에 연결된 모든 리프레시 토큰 일괄 삭제

}
