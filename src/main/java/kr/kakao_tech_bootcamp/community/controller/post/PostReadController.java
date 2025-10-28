package kr.kakao_tech_bootcamp.community.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.GetPostDetailResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
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

    @GetMapping
    @Operation(summary = "모든 게시글 조회", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Slice<AllPostResponseDto>>> getAllPosts(
            HttpServletRequest request,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt")
            Pageable pageable){
        Slice<AllPostResponseDto> postSlice= postService.getAllPosts(request, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "게시글 전체 조회에 성공했습니다.", postSlice));
    }



    @GetMapping( "/{postId}")
    @Operation(summary = "게시글 조회", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<GetPostDetailResponseDto>> getPost(
            HttpServletRequest request,
            @PathVariable int postId
    ){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "게시글을 성공적으로 조회했습니다.", postService.getPostDetail(request, postId)));
    }


}
