package kr.kakao_tech_bootcamp.community.manager;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class PostViewCountManager {
    private final ConcurrentHashMap<Integer, Integer> postViewCountMap = new ConcurrentHashMap<>();

    public void increaseViewCount(int postId) {
        postViewCountMap.put(postId, postViewCountMap.getOrDefault(postId, 0) + 1);
    }

    public int getPostViewCount(int postId) {
        return postViewCountMap.getOrDefault(postId, 0);
    }

    public void clear(){
        postViewCountMap.clear();
    }
}
