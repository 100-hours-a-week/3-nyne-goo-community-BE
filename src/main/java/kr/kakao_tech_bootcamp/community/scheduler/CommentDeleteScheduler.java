package kr.kakao_tech_bootcamp.community.scheduler;

import kr.kakao_tech_bootcamp.community.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Component
@RequiredArgsConstructor
public class CommentDeleteScheduler {

    private final CommentRepository commentRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void permanentlyDeleteComments() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        commentRepository.deleteByDeletedAtBefore(cutoff);
        log.info("30일 지난 삭제된 게시글 영구 삭제 완료 (" + cutoff + " 지남)");
    }
}
