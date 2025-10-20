package kr.kakao_tech_bootcamp.community.scheduler;

import jakarta.annotation.PostConstruct;
import kr.kakao_tech_bootcamp.community.manager.PostViewCountManager;
import kr.kakao_tech_bootcamp.community.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
@RequiredArgsConstructor
public class PostViewCountScheduler {
    private final PostRepository postRepository;
    private final PostViewCountManager postViewCountManager;


    // 1분마다 실행
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updatePostViewCounts() {
        ConcurrentHashMap<Integer, Integer> postLikeCountMap = postViewCountManager.getPostViewCountMap();

        if(postLikeCountMap.isEmpty()) return;

        postLikeCountMap.forEach(postRepository::updatePostViewsCount);

        postViewCountManager.clear();
        log.info("게시글 조회수 반영 완료");
    }
}
