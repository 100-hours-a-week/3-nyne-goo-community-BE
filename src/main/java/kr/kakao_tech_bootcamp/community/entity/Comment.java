package kr.kakao_tech_bootcamp.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name="comments")
@SQLDelete(sql = "UPDATE comments SET deleted_at = NOW() WHERE id = ? ")
@SQLRestriction("deleted_at IS NULL")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    protected Comment() {}

    public Comment(String content, User user, Post post) {
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.user = user;
        this.post = post;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void deleteComment(){
        this.deletedAt = LocalDateTime.now();
    }
}
