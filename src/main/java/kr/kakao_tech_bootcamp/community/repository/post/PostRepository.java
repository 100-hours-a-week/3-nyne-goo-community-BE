package kr.kakao_tech_bootcamp.community.repository.post;

import kr.kakao_tech_bootcamp.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>, PostQueryRepository {
    @Modifying
    @Query("delete from Post p where p.deletedAt is not null and p.deletedAt < :cutoffDate")
    void deleteByDeletedAtBefore(LocalDateTime cutoffDate);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.viewsCount = p.viewsCount + :count where p.id = :postId")
    void updatePostViewsCount(@Param("postId") int postId, @Param("count") int count);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.commentsCount = p.commentsCount + :count where p.id = :postId")
    void updatePostCommentsCount(@Param("postId") int postId, @Param("count") int count);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.likesCount = p.likesCount + :count where p.id = :postId")
    void updatePostLikesCount(@Param("postId") int postId, @Param("count") int count);


    @Query("select p from Post p join fetch p.user where p.id = :id")
    Optional<Post> findByIdWithUser(@Param("id") int id);
}
