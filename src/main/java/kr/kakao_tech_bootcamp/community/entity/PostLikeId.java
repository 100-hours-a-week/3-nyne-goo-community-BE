package kr.kakao_tech_bootcamp.community.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

// user_id, post_id를 하나의 객체로 만들어서 post_likes의 식별자임을 JPA에 알림
// 하나의 객체로 만들어서 비교 로직을 한 번만 작성하면 됨
@Embeddable
public class PostLikeId implements Serializable {
    private int userId;
    private int postId;

    protected PostLikeId() {}

    public PostLikeId(int userId, int postId) {
        this.userId = userId;
        this.postId = postId;
    }

    // 같은 키 객체인지 확인
    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null || obj.getClass()!=this.getClass()) return false;

        PostLikeId other = (PostLikeId)obj;
        return this.userId==other.userId && this.postId==other.postId;
    }
}
