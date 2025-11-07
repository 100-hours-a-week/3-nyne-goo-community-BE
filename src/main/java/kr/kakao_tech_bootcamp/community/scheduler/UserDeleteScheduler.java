package kr.kakao_tech_bootcamp.community.scheduler;

import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import kr.kakao_tech_bootcamp.community.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class UserDeleteScheduler {
    private final UserRepository userRepository;  // DB 접근
    private final ImageStorageService imageStorageService;

    // 매일 새벽 3시에 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void permanentlyDeleteOldUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        // 삭제 대상 유저 이미지 UUID 먼저 수집
        List<String> uuids = userRepository.findAllUuidsByUserDeletedAtBefore(cutoff);
        userRepository.deleteByDeletedAtBefore(cutoff);

        // 트랜잭션 커밋된 후 파일 삭제
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        int success = 0, fail = 0;
                        for (String id : uuids) {
                            try {
                                imageStorageService.deleteImage(id); // 실제 파일 삭제
                                success++;
                            } catch (Exception e) {
                                fail++;
                            }
                        }
                        log.info("유저 영구 삭제 완료. cutoff={}, files deleted={}, failed={}", cutoff, success, fail);
                    }
                }
        );
    }

    // 1분마다 status == deleted인 데이터 삭제
   /* @Scheduled(fixedRate = 60000)
    @Transactional
    public void permanentlyDeleteOldUsers() {
        userRepository.deleteByUserStatus(UserStatus.DELETED);
        System.out.println("status가 DELETED인 유저 전부 영구 삭제 완료");
    }*/

}
