package kr.kakao_tech_bootcamp.community.repository.post;

import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.GetPostDetailResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostQueryRepository {
    // 인피티니 스크롤(무한 스크롤)이니까 slice 사용
    Slice<AllPostResponseDto> getAllPosts(int userId, Pageable pageable);

    Optional<GetPostDetailResponseDto> getPostByPostId(int userId, int postId);
}
