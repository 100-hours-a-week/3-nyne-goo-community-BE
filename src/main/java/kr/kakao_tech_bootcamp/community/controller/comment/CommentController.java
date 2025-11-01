package kr.kakao_tech_bootcamp.community.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.comment.ChangeCommentRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.comment.CreateCommentRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.AllCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.ChangeCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.CreateCommentResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "게시물의 댓글 조회")
    public ResponseEntity<ApiResponse<Slice<AllCommentResponseDto>>> getCommentsByPostId(
            HttpServletRequest request,
            @PathVariable int postId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "댓글 리스트를 성공적으로 조회했습니다.",
                        commentService.getAllComments(request, postId, pageable)));
    }

    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "댓글 작성")
    public ResponseEntity<ApiResponse<CreateCommentResponseDto>> createComment(HttpServletRequest request, @PathVariable int postId, @RequestBody CreateCommentRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "댓글을 등록했습니다.", commentService.createComment(request, postId, requestDto)));
    }

    @PatchMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<ApiResponse<ChangeCommentResponseDto>> changeComment(HttpServletRequest request, @PathVariable int commentId, @RequestBody ChangeCommentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "댓글을 수정했습니다.",
                        commentService.changeComment(request, commentId, requestDto)));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteComment(HttpServletRequest request, @PathVariable int commentId) {
        commentService.deleteComment(request, commentId);
        return ResponseEntity.ok(ApiResponse.success(200, "댓글을 삭제했습니다."));
    }
}
