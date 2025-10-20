package kr.kakao_tech_bootcamp.community.scheduler;

import jakarta.annotation.PostConstruct;
import kr.kakao_tech_bootcamp.community.manager.PostCommentCountManager;
import kr.kakao_tech_bootcamp.community.manager.PostLikeCountManager;
import kr.kakao_tech_bootcamp.community.repository.post.PostLikeRepository;
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
public class PostLikeCountScheduler {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeCountManager postLikeCountManager;

    // 1분마다 실행
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void flushPostLikeCounts(){
        ConcurrentHashMap<Integer, Integer> postLikeCountMap = postLikeCountManager.getPostLikeCountMap();

        if(postLikeCountMap.isEmpty()) return;

        postLikeCountMap.forEach(postRepository::updatePostLikesCount);

        postLikeCountManager.clear();
        log.info("게시글 좋아요수 반영 완료");
    }
}
