package kr.kakao_tech_bootcamp.community.repository.comment;

import kr.kakao_tech_bootcamp.community.dto.response.comment.AllCommentResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

public interface CommentQueryRepository {
    // 댓글 인피니티 스크롤 적용
    Slice<AllCommentResponseDto> getAllComments(@Param("userId") int userId, @Param("postId") int postId,  @Param("pageable") Pageable pageable);
}
