package kr.kakao_tech_bootcamp.community.entity;

import jakarta.persistence.*;

// IdClass: 간단하지만 객체지향적이지 않고 나중에 pk 로직 필요하면 불편
// embeddedId: pk를 하나의 값 객체로 다루어 객체지향적이지만 매핑이 복잡함
// -> 객체 지향적 설계를 위해 embeeddedId로 설계


@Entity
@Table(name="post_likes")
public class PostLike {
    @EmbeddedId
    private PostLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name="post_id")
    private Post post;

    protected PostLike() {}

    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
        this.id = new PostLikeId(user.getId(), post.getId());
    }
}
