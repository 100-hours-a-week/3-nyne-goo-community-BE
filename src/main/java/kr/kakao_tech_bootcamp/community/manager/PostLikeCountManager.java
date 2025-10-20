package kr.kakao_tech_bootcamp.community.manager;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class PostLikeCountManager {
    private final ConcurrentHashMap<Integer, Integer> postLikeCountMap = new ConcurrentHashMap<>();

    public void increaseLike(int postId) {
        postLikeCountMap.put(postId, postLikeCountMap.getOrDefault(postId, 0) + 1);
    }

    public void decreaseLike(int postId) {
        int count = postLikeCountMap.getOrDefault(postId, 0);
        postLikeCountMap.put(postId, count == 0 ? 0 : count - 1);
    }

    public int  getPostLikeCount(int postId) {
        return postLikeCountMap.getOrDefault(postId, 0);
    }

    public void clear(){
        postLikeCountMap.clear();
    }
}
