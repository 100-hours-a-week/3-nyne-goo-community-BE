package kr.kakao_tech_bootcamp.community.service;

import jakarta.servlet.http.HttpServletRequest;
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
    private final AuthHelper authHelper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostCommentCountManager postCommentCountManager;

    // 댓글 작성
    public CreateCommentResponseDto createComment(HttpServletRequest request, int postId, CreateCommentRequestDto createCommentRequestDto){
        // 댓글 길이 확인
        if(createCommentRequestDto.getContent().isEmpty() || createCommentRequestDto.getContent().length()>500) throw new RestApiException(CommentErrorCode.INVALID_COMMENT);

        User user = authHelper.findUserFromRequest(request);
        Post post = postRepository.getReferenceById(postId);

        Comment comment = new Comment(createCommentRequestDto.getContent(), user, post);
        commentRepository.save(comment);

        postCommentCountManager.increaseComment(postId);

        return CreateCommentResponseDto.from(comment);
    }

    // 모든 댓글 리스트 조회
    @Transactional(readOnly = true)
    public Slice<AllCommentResponseDto> getAllComments(HttpServletRequest request, int postId, Pageable pageable) {
        return commentRepository.getAllComments(authHelper.findUserFromRequest(request).getId(), postId, pageable);
    }

    // 댓글 수정
    public ChangeCommentResponseDto changeComment(HttpServletRequest request, int commentId, ChangeCommentRequestDto changeCommentRequestDto) {
        // 댓글 길이 확인
        if(changeCommentRequestDto.getContent().isEmpty() || changeCommentRequestDto.getContent().length()>500) throw new RestApiException(CommentErrorCode.INVALID_COMMENT);

        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new RestApiException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(authHelper.findUserFromRequest(request).getId())) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN);
        }

        comment.updateContent(changeCommentRequestDto.getContent());

        return ChangeCommentResponseDto.from(comment);
    }

    // 댓글 삭제
    public void deleteComment(HttpServletRequest request, int commentId) {
        Comment comment = commentRepository.findByIdWithUserAndPost(commentId)
                .orElseThrow(() -> new RestApiException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().equals(authHelper.findUserFromRequest(request))) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN);
        }

        comment.deleteComment();
        postCommentCountManager.decreaseComment(comment.getPost().getId());
    }
}