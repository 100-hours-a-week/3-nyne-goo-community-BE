package kr.kakao_tech_bootcamp.community.scheduler;

import kr.kakao_tech_bootcamp.community.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Component
@RequiredArgsConstructor
public class PostDeleteScheduler {
    private final PostRepository postRepository;

    // 매일 3시마다 30일 지난 게시글 있다면 삭제
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void permanentlyDeletePosts() {
        LocalDateTime cutoff =  LocalDateTime.now().minusDays(30);
        postRepository.deleteByDeletedAtBefore(cutoff);
        log.info("30일 지난 삭제된 게시글 영구 삭제 완료 (" + cutoff + " 지남)");
    }

    // 1분마다 deleted_at != null인 데이터 삭제
    /*@Scheduled(fixedRate = 60000)
    @Transactional
    public void permanentlyDeletePosts() {
        postRepository.deleteByDeletedAtIsNotNull();
        log.info("삭제된 게시글 영구 삭제 완료");
    }*/
}
