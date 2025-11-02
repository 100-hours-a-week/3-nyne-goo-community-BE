package kr.kakao_tech_bootcamp.community.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.response.post.PostLikeResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.PostLikeCountManager;
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
    private final JwtProvider jwtProvider;

    @PostMapping
    @Operation(summary = "좋아요 등록")
    public ResponseEntity<ApiResponse<PostLikeResponseDto>> postLike(HttpServletRequest request, @PathVariable int postId){
        int userId = jwtProvider.extractUserIdFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "좋아요가 등록되었습니다.", postLikeService.createPostLike(userId, postId)));
    }

    @DeleteMapping
    @Operation(summary = "좋아요 취소")
    public ResponseEntity<ApiResponse<PostLikeResponseDto>> deleteLike(HttpServletRequest request, @PathVariable int postId){
        int userId = jwtProvider.extractUserIdFromRequest(request);
        return ResponseEntity.ok(ApiResponse.success(200, "좋아요가 취소되었습니다.", postLikeService.deletePostLike(userId, postId)));
    }
}
