package kr.kakao_tech_bootcamp.community.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import kr.kakao_tech_bootcamp.community.dto.ApiResponse;
import kr.kakao_tech_bootcamp.community.dto.request.post.CreatePostRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.post.UpdatePostRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.CreatePostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.GetPostDetailResponseDto;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final JwtProvider jwtProvider;

    @GetMapping
    @Operation(summary = "모든 게시글 조회")
    public ResponseEntity<ApiResponse<Slice<AllPostResponseDto>>> getAllPosts(
            HttpServletRequest request,
            @ParameterObject
            Pageable pageable){
        int userId = jwtProvider.extractUserIdFromRequest(request);
        Slice<AllPostResponseDto> postSlice= postService.getAllPosts(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "게시글 전체 조회에 성공했습니다.", postSlice));
    }

    @GetMapping( "/{postId}")
    @Operation(summary = "게시글 조회", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<GetPostDetailResponseDto>> getPost(
            HttpServletRequest request,
            @PathVariable int postId
    ){
        int userId = jwtProvider.extractUserIdFromRequest(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "게시글을 성공적으로 조회했습니다.", postService.getPostDetail(userId, postId)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 생성", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<CreatePostResponseDto>> createPost(
            HttpServletRequest request,
            @ModelAttribute CreatePostRequestDto createPostRequestDto,
            @RequestPart(value="images", required=false) List<MultipartFile> imageList
    ){
        int userId = jwtProvider.extractUserIdFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "게시글을 생성했습니다.", postService.createPost(userId, createPostRequestDto, imageList)));
    }

    @PatchMapping(path = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 수정", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Void>>  updatePost(
            HttpServletRequest request,
            @PathVariable int postId,
            @ModelAttribute UpdatePostRequestDto updatePostRequestDto,
            @RequestPart(value="images", required = false) List<MultipartFile> imageList
    ){
        int userId = jwtProvider.extractUserIdFromRequest(request);
        postService.updatePost(userId, postId, updatePostRequestDto, imageList);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(200, "게시글 수정에 성공했습니다."));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<ApiResponse<Void>> deletePost(
            HttpServletRequest request,
            @PathVariable int postId
    ){
        int userId = jwtProvider.extractUserIdFromRequest(request);
        postService.deletePost(userId, postId);

        return ResponseEntity.ok(ApiResponse.success(200, "게시글을 삭제했습니다."));
    }
}
