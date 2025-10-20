package kr.kakao_tech_bootcamp.community.service;

import jakarta.persistence.EntityNotFoundException;
import kr.kakao_tech_bootcamp.community.dto.request.comment.ChangeCommentRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.comment.CreateCommentRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.AllCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.ChangeCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.CreateCommentResponseDto;
import kr.kakao_tech_bootcamp.community.entity.Comment;
import kr.kakao_tech_bootcamp.community.entity.Post;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.exception.ForbiddenException;
import kr.kakao_tech_bootcamp.community.exception.UnauthorizedException;
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
        int userId = jwtProvider.getIdFromToken(token);

        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException();
        }

        comment.setContent(changeCommentRequestDto.getContent());

        return ChangeCommentResponseDto.from(comment);
    }

    public void deleteComment(String token, int commentId) {
        int userId = jwtProvider.getIdFromToken(token);

        Comment comment = commentRepository.findByIdWithUserAndPost(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException();
        }

        commentRepository.delete(comment);

        postCommentCountManager.decreaseComment(comment.getPost().getId());
    }
}
