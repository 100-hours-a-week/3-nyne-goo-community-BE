package kr.kakao_tech_bootcamp.community.repository.post;

import kr.kakao_tech_bootcamp.community.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    void deleteByUserIdAndPostId(int userId, int postId);

    boolean existsByUserIdAndPostId(int userId, int postId);
}
