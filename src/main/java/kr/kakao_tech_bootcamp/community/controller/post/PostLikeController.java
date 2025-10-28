package kr.kakao_tech_bootcamp.community.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.response.post.PostLikeResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.SessionManager;
import kr.kakao_tech_bootcamp.community.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {
    private final PostLikeService postLikeService;
    private final SessionManager sessionManager;

    @PostMapping
    @Operation(summary = "좋아요 등록")
    public ResponseEntity<ApiResponse<PostLikeResponseDto>> postLike(HttpServletRequest request, HttpServletResponse response, @PathVariable int postId){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "좋아요가 등록되었습니다.", postLikeService.createPostLike(sessionManager.getSession(request, response), postId)));
    }

    @DeleteMapping
    @Operation(summary = "좋아요 취소")
    public ResponseEntity<ApiResponse<PostLikeResponseDto>> deleteLike(HttpServletRequest request, HttpServletResponse response, @PathVariable int postId){
        return ResponseEntity.ok(ApiResponse.success(200, "좋아요가 취소되었습니다.", postLikeService.deletePostLike(sessionManager.getSession(request, response), postId)));
    }
}
