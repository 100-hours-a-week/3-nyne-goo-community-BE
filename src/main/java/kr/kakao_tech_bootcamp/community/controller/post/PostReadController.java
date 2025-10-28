package kr.kakao_tech_bootcamp.community.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.GetPostDetailResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.SessionManager;
import kr.kakao_tech_bootcamp.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostReadController {
    private final PostService postService;
    private final SessionManager sessionManager;

    @GetMapping
    @Operation(summary = "모든 게시글 조회", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Slice<AllPostResponseDto>>> getAllPosts(
            HttpServletRequest request,
            HttpServletResponse response,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt")
            Pageable pageable){

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "게시글 전체 조회에 성공했습니다.", postService.getAllPosts(sessionManager.getSession(request, response), pageable)));
    }



    @GetMapping( "/{postId}")
    @Operation(summary = "게시글 조회", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<GetPostDetailResponseDto>> getPost(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable int postId
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "게시글을 성공적으로 조회했습니다.", postService.getPostDetail(sessionManager.getSession(request, response), postId)));
    }


}
