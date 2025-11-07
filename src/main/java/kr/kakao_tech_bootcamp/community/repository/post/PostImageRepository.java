package kr.kakao_tech_bootcamp.community.repository.post;

import kr.kakao_tech_bootcamp.community.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost_Id(int postId);

    @Query("select pi.imageUUID from PostImage pi where pi.post.deletedAt < :cutoff")
    List<String> findAllUuidsByPostDeletedAtBefore(LocalDateTime cutoff);

    @Query("delete from PostImage pi where pi.post.deletedAt < :cutoff")
    void deleteAllByPostDeletedAtBefore(LocalDateTime cutoff);
}
