package kr.kakao_tech_bootcamp.community.scheduler;

import kr.kakao_tech_bootcamp.community.repository.post.PostImageRepository;
import kr.kakao_tech_bootcamp.community.repository.post.PostRepository;
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
public class PostDeleteScheduler {
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final ImageStorageService imageStorageService;

    // 매일 3시마다 30일 지난 게시글 있다면 삭제
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void permanentlyDeletePosts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        // 삭제 대상 게시글들의 이미지 UUID 먼저 수집
        List<String> uuids = postImageRepository.findAllUuidsByPostDeletedAtBefore(cutoff);

        // db 에서 삭제
        postRepository.deleteByDeletedAtBefore(cutoff);
        postImageRepository.deleteAllByPostDeletedAtBefore(cutoff);

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
                        log.info("게시글 영구 삭제 완료. cutoff={}, files deleted={}, failed={}", cutoff, success, fail);
                    }
                }
        );
    }
}
