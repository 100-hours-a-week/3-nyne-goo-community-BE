package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.dto.SessionUserDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.PostLikeResponseDto;
import kr.kakao_tech_bootcamp.community.entity.Post;
import kr.kakao_tech_bootcamp.community.entity.PostLike;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommentErrorCode;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
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
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeCountManager postLikeCountManager;

    public PostLikeResponseDto createPostLike(SessionUserDto sessionUserDto, int postId){
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        Post post = postRepository.getReferenceById(postId);

        if(postLikeRepository.existsByUserIdAndPostId(user.getId(), postId)) throw new RestApiException(CommonErrorCode.CONFLICT);

        PostLike postLike = new PostLike(user, post);

        postLikeCountManager.increaseLike(postId);

        postLikeRepository.save(postLike);

        return PostLikeResponseDto.of(postId, post.getLikesCount()+postLikeCountManager.getPostLikeCount(postId));
    }

    public PostLikeResponseDto deletePostLike(SessionUserDto sessionUserDto, int postId){
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        Post post = postRepository.getReferenceById(postId);

        if(!postLikeRepository.existsByUserIdAndPostId(user.getId(), postId)) throw new RestApiException(CommonErrorCode.CONFLICT);

        postLikeCountManager.decreaseLike(postId);

        postLikeRepository.deleteByUserIdAndPostId(user.getId(), postId);

        return PostLikeResponseDto.of(postId, post.getLikesCount()+postLikeCountManager.getPostLikeCount(postId));
    }
}
