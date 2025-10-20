package kr.kakao_tech_bootcamp.community.repository.comment;

import kr.kakao_tech_bootcamp.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>, CommentQueryRepository {
    @Query("select c from Comment c join fetch c.user where c.id = :id")
    Optional<Comment> findByIdWithUser(@Param("id") int id);

    // 댓글 삭제 시 쿼리 한번만 날리기 위해 필요
    @Query("select c from Comment c join fetch c.user join fetch c.post where c.id = :id")
    Optional<Comment> findByIdWithUserAndPost(@Param("id") int id);

    @Modifying
    @Query("delete from Comment c where c.deletedAt is not null and c.deletedAt < :cutoffDate")
    void deleteByDeletedAtBefore(LocalDateTime cutoffDate);
}
