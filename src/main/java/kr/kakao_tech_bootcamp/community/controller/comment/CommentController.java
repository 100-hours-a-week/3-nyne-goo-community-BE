package kr.kakao_tech_bootcamp.community.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.comment.ChangeCommentRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.comment.CreateCommentRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.AllCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.ChangeCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.comment.CreateCommentResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.SessionManager;
import kr.kakao_tech_bootcamp.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/{commentId}")
public class CommentController {
    private final CommentService commentService;
    private final SessionManager sessionManager;

    @PatchMapping
    @Operation(summary = "댓글 수정")
    public ResponseEntity<ApiResponse<ChangeCommentResponseDto>> changeComment(HttpServletRequest request, HttpServletResponse response, @PathVariable int commentId, @RequestBody ChangeCommentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "댓글을 수정했습니다.",
                        commentService.changeComment(sessionManager.getSession(request, response), commentId, requestDto)));
    }

    @DeleteMapping
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteComment(HttpServletRequest request, HttpServletResponse response, @PathVariable int commentId) {
        commentService.deleteComment(sessionManager.getSession(request, response), commentId);
        return ResponseEntity.ok(ApiResponse.success(200, "댓글을 삭제했습니다."));
    }
}
