package kr.kakao_tech_bootcamp.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name="post_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name="image_path")
    private String imagePath;

    @Column(nullable = false, name="image_original_name")
    private String imageName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int imageOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    public PostImage(String imagePath, String imageName, int imageOrder, Post post) {
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.createdAt = LocalDateTime.now();
        this.imageOrder = imageOrder;
        this.post = post;
    }
}