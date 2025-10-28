package kr.kakao_tech_bootcamp.community.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.comment.CreateCommentRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.AllCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.CreateCommentResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.SessionManager;
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
@RequestMapping("/posts/{postId}/comments")
public class PostCommentController {
    private final CommentService commentService;
    private final SessionManager sessionManager;

    @GetMapping
    @Operation(summary = "게시물의 댓글 조회")
    public ResponseEntity<ApiResponse<Slice<AllCommentResponseDto>>> getCommentsByPostId(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable int postId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "댓글 리스트를 성공적으로 조회했습니다.",
                        commentService.getAllComments(sessionManager.getSession(request, response), postId, pageable)));
    }

    @PostMapping
    @Operation(summary = "댓글 작성")
    public ResponseEntity<ApiResponse<CreateCommentResponseDto>> createComment(HttpServletRequest request, HttpServletResponse response,@PathVariable int postId, @RequestBody CreateCommentRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "댓글을 등록했습니다.", commentService.createComment(sessionManager.getSession(request, response), postId, requestDto)));
    }

}
