package kr.kakao_tech_bootcamp.community.repository.post;

import kr.kakao_tech_bootcamp.community.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost_Id(int postId);
}
