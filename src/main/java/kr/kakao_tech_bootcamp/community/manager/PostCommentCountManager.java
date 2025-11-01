package kr.kakao_tech_bootcamp.community.manager;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class PostCommentCountManager {
    private final ConcurrentHashMap<Integer, Integer> postCommentCountMap = new ConcurrentHashMap<>();

    public void increaseComment(int postId) {
        postCommentCountMap.put(postId, postCommentCountMap.getOrDefault(postId, 0) + 1);
    }

    public void decreaseComment(int postId) {
        int count = postCommentCountMap.getOrDefault(postId, 0);
        postCommentCountMap.put(postId, count == 0 ? 0 : count - 1);
    }

    public int getPostCommentCount(int postId) {
        return postCommentCountMap.getOrDefault(postId, 0);
    }

    public Map<Integer, Integer> getAllCommentsCount() {
        return postCommentCountMap;
    }

    public void clear(){
        postCommentCountMap.clear();
    }
}
