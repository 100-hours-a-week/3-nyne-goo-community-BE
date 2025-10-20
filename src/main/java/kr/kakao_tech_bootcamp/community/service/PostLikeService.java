package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.dto.response.post.PostLikeResponseDto;
import kr.kakao_tech_bootcamp.community.entity.Post;
import kr.kakao_tech_bootcamp.community.entity.PostLike;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.PostLikeCountManager;
import kr.kakao_tech_bootcamp.community.repository.post.PostLikeRepository;
import kr.kakao_tech_bootcamp.community.repository.post.PostRepository;
import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {
    private final JwtProvider jwtProvider;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostLikeCountManager postLikeCountManager;

    public PostLikeResponseDto createPostLike(String token, int postId){
        int userId = jwtProvider.getIdFromToken(token);

        User user = userRepository.getReferenceById(userId);
        Post post = postRepository.getReferenceById(postId);

        if(postLikeRepository.existsByUserIdAndPostId(userId, postId)) throw new IllegalArgumentException("이미 좋아요를 누른 게시글입니다.");

        PostLike postLike = new PostLike(user, post);

        postLikeCountManager.increaseLike(postId);

        postLikeRepository.save(postLike);

        return PostLikeResponseDto.of(postId, post.getLikesCount()+postLikeCountManager.getPostLikeCount(postId));
    }

    public PostLikeResponseDto deletePostLike(String token, int postId){
        int userId = jwtProvider.getIdFromToken(token);

        User user = userRepository.getReferenceById(userId);
        Post post = postRepository.getReferenceById(postId);

        if(!postLikeRepository.existsByUserIdAndPostId(userId, postId)) throw new IllegalArgumentException("좋아요를 누르지 않은 게시글입니다.");

        postLikeCountManager.decreaseLike(postId);

        postLikeRepository.deleteByUserIdAndPostId(userId, postId);

        return PostLikeResponseDto.of(postId, post.getLikesCount()+postLikeCountManager.getPostLikeCount(postId));
    }
}
