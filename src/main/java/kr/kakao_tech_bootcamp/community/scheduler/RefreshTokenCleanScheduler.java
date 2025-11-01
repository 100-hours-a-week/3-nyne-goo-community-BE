package kr.kakao_tech_bootcamp.community.scheduler;

import kr.kakao_tech_bootcamp.community.repository.RefreshTokenRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Log4j2
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanScheduler {
    private final RefreshTokenRepository refreshTokenRepository;

    // 1분마다 만료된 토큰 삭제
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanRefreshTokens() {
        refreshTokenRepository.deleteExpiredRefreshTokens(Instant.now());
        log.info("만료된 refresh token 삭제 완료");
    }

}
