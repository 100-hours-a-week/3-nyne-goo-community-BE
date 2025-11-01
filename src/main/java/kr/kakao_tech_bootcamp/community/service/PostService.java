package kr.kakao_tech_bootcamp.community.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.kakao_tech_bootcamp.community.dto.request.post.CreatePostRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.post.UpdatePostRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.CreatePostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.GetPostDetailResponseDto;
import kr.kakao_tech_bootcamp.community.entity.*;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.exception.error_code.PostErrorCode;
import kr.kakao_tech_bootcamp.community.jwt.JwtProvider;
import kr.kakao_tech_bootcamp.community.manager.PostCommentCountManager;
import kr.kakao_tech_bootcamp.community.manager.PostLikeCountManager;
import kr.kakao_tech_bootcamp.community.manager.PostViewCountManager;
import kr.kakao_tech_bootcamp.community.repository.post.PostRepository;
import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final AuthHelper authHelper;
    private final PostRepository postRepository;
    private final PostImageService postImageService;
    private final PostViewCountManager postViewCountManager;
    private final PostCommentCountManager postCommentCountManager;
    private final PostLikeCountManager postLikeCountManager;

    public Slice<AllPostResponseDto> getAllPosts(HttpServletRequest request, Pageable pageable) {
        int userId = authHelper.findUserFromRequest(request).getId();
        Slice<AllPostResponseDto> allPosts = postRepository.getAllPosts(userId, pageable);

        // DB에서 가져온 값들 중 postId만 따로 뽑아 리스트로 저장
        List<Integer> postIds = allPosts.getContent().stream().map(AllPostResponseDto::getPostId).toList();
        // 매니저에서 저장된 수들 가져옴
        Map<Integer, Integer> like = postLikeCountManager.getAllPostLikeCount();
        Map<Integer, Integer> view = postViewCountManager.getAllPostViewCount();
        Map<Integer, Integer> comment = postCommentCountManager.getAllCommentsCount();

        // DB에서 가져온 값과 매니저에 저장된 값 더해서 새 객체로 리스트에 저장
        List<AllPostResponseDto> allPostResponseDtoList = allPosts.getContent().stream().map(posts -> {
            int likeCount = posts.getLikesCount()+like.getOrDefault(posts.getPostId(), 0);
            int commentCount = posts.getCommentsCount() + comment.getOrDefault(posts.getPostId(), 0);
            int viewCount = posts.getViewsCount()+view.getOrDefault(posts.getPostId(), 0);

            return AllPostResponseDto.plusCounts(posts, likeCount, commentCount, viewCount);
        }).toList();

        return new SliceImpl<>(allPostResponseDtoList, pageable, allPosts.hasNext());
    }

    public CreatePostResponseDto createPost(HttpServletRequest request, CreatePostRequestDto createPostRequestDto, List<MultipartFile> imageList) {
        if(createPostRequestDto.getTitle().isEmpty() || createPostRequestDto.getTitle().length()>26) throw new RestApiException(PostErrorCode.INVALID_TITLE);
        if(createPostRequestDto.getContent().isEmpty() || createPostRequestDto.getContent().length()>2000) throw new RestApiException(PostErrorCode.INVALID_CONTENT);

        User user = authHelper.findUserFromRequest(request);
        Post post = new Post(createPostRequestDto.getTitle(), createPostRequestDto.getContent(), user);

        if (imageList != null && !imageList.isEmpty()) {
            List<PostImage> postImageList = postImageService.createPostImages(imageList, post);
            post.getImages().addAll(postImageList);
        }

        return CreatePostResponseDto.from(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public GetPostDetailResponseDto getPostDetail(HttpServletRequest request, int postId) {
        int userId = authHelper.findUserFromRequest(request).getId();
        GetPostDetailResponseDto postDetail = postRepository.getPostByPostId(userId, postId).orElseThrow(() -> new RestApiException(CommonErrorCode.NOT_FOUND));

        postViewCountManager.increaseViewCount(postId);

        int likeCount = postLikeCountManager.getPostLikeCount(postId);
        int commentCount = postCommentCountManager.getPostCommentCount(postId);
        int viewCount = postViewCountManager.getPostViewCount(postId);

        return postDetail.plusCounts(likeCount, commentCount, viewCount);
    }

    public void updatePost(HttpServletRequest request, int postId, UpdatePostRequestDto updatePostRequestDto, List<MultipartFile> imageList) {
        if(updatePostRequestDto.getTitle().isEmpty() || updatePostRequestDto.getTitle().length()>26) throw new RestApiException(PostErrorCode.INVALID_TITLE);
        if(updatePostRequestDto.getContent().isEmpty() || updatePostRequestDto.getContent().length()>2000) throw new RestApiException(PostErrorCode.INVALID_CONTENT);

        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new RestApiException(CommonErrorCode.NOT_FOUND));
        User user = authHelper.findUserFromRequest(request);

        if (!post.getUser().equals(user)) throw new RestApiException(CommonErrorCode.FORBIDDEN);

        post.setTitle(updatePostRequestDto.getTitle());
        post.setContent(updatePostRequestDto.getContent());
        post.setUpdatedAt();

        for (PostImage prevImage : post.getImages()) {
            Path path = Paths.get(System.getProperty("user.dir") + "/uploads/" + prevImage.getImageUUID());
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("이미지 파일 삭제 실패: " + path + "\ner ror:" + e.getMessage());
                throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        post.getImages().clear();
        List<PostImage> postImageList = postImageService.createPostImages(imageList, post);
        post.getImages().addAll(postImageList);

        postRepository.save(post);
    }

    public void deletePost(HttpServletRequest request, int postId) {
        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new RestApiException(CommonErrorCode.NOT_FOUND));
        User user = authHelper.findUserFromRequest(request);

        if (!post.getUser().equals(user)) throw new RestApiException(CommonErrorCode.FORBIDDEN);

        if(post.getDeletedAt()!=null) throw new RestApiException(CommonErrorCode.BAD_REQUEST);

        post.deletePost();
    }
}
