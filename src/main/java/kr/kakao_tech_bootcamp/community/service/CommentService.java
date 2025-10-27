package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.dto.request.comment.ChangeCommentRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.comment.CreateCommentRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.AllCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.ChangeCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.CreateCommentResponseDto;
import kr.kakao_tech_bootcamp.community.entity.Comment;
import kr.kakao_tech_bootcamp.community.entity.Post;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommentErrorCode;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.PostCommentCountManager;
import kr.kakao_tech_bootcamp.community.repository.comment.CommentRepository;
import kr.kakao_tech_bootcamp.community.repository.post.PostRepository;
import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final JwtProvider jwtProvider;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostCommentCountManager postCommentCountManager;

    public CreateCommentResponseDto createComment(String token, int postId, CreateCommentRequestDto createCommentRequestDto){
        if(createCommentRequestDto.getContent().isEmpty()) throw new RestApiException(CommentErrorCode.EMPTY_CONTENT);
        if(createCommentRequestDto.getContent().length()>500) throw new RestApiException(CommentErrorCode.TOO_LONG_CONTENT);

        int userId = jwtProvider.getIdFromToken(token);

        User user = userRepository.getReferenceById(userId);
        Post post = postRepository.getReferenceById(postId);

        Comment comment = new Comment(createCommentRequestDto.getContent(), user, post);
        commentRepository.save(comment);

        postCommentCountManager.increaseComment(postId);

        return CreateCommentResponseDto.from(comment);
    }

    @Transactional(readOnly = true)
    public Slice<AllCommentResponseDto> getAllComments(String token, int postId, Pageable pageable) {
        int userId = jwtProvider.getIdFromToken(token);
        return commentRepository.getAllComments(userId, postId, pageable);
    }

    public ChangeCommentResponseDto changeComment(String token, int commentId, ChangeCommentRequestDto changeCommentRequestDto) {
        if(changeCommentRequestDto.getContent().isEmpty()) throw new RestApiException(CommentErrorCode.EMPTY_CONTENT);
        if(changeCommentRequestDto.getContent().length()>500) throw new RestApiException(CommentErrorCode.TOO_LONG_CONTENT);

        int userId = jwtProvider.getIdFromToken(token);

        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new RestApiException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN);
        }

        comment.setContent(changeCommentRequestDto.getContent());

        return ChangeCommentResponseDto.from(comment);
    }

    public void deleteComment(String token, int commentId) {
        int userId = jwtProvider.getIdFromToken(token);

        Comment comment = commentRepository.findByIdWithUserAndPost(commentId)
                .orElseThrow(() -> new RestApiException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN);
        }

        commentRepository.delete(comment);

        postCommentCountManager.decreaseComment(comment.getPost().getId());
    }
}
