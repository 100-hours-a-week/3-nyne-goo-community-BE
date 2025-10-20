package kr.kakao_tech_bootcamp.community.scheduler;

import jakarta.annotation.PostConstruct;
import kr.kakao_tech_bootcamp.community.manager.PostCommentCountManager;
import kr.kakao_tech_bootcamp.community.repository.comment.CommentRepository;
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
public class PostCommentCountScheduler {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostCommentCountManager postCommentCountManager;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updatePostCommentCount() {
        ConcurrentHashMap<Integer, Integer> postCommentCountMap = postCommentCountManager.getPostCommentCountMap();

        if(postCommentCountMap.isEmpty()) return;

        postCommentCountMap.forEach(postRepository::updatePostCommentsCount);

        postCommentCountManager.clear();
        log.info("게시글 댓글수 반영 완료");
    }
}
