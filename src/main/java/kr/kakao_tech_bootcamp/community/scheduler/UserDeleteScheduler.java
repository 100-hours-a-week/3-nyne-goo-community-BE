package kr.kakao_tech_bootcamp.community.scheduler;

import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Component
@RequiredArgsConstructor
public class UserDeleteScheduler {
    private final UserRepository userRepository;  // DB 접근

    // 매일 새벽 3시에 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void permanentlyDeleteOldUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        userRepository.deleteByDeletedAtBefore(cutoff);
        log.info("30일 지난 DELETED 유저 영구 삭제 완료 (" + cutoff + " 지남)");
    }

    // 1분마다 status == deleted인 데이터 삭제
   /* @Scheduled(fixedRate = 60000)
    @Transactional
    public void permanentlyDeleteOldUsers() {
        userRepository.deleteByUserStatus(UserStatus.DELETED);
        System.out.println("status가 DELETED인 유저 전부 영구 삭제 완료");
    }*/

}
