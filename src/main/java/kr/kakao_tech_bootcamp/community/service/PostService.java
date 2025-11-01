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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        Slice<AllPostResponseDto> allPosts = postRepository.getAllPosts(authHelper.findUserFromRequest(request).getId(), pageable);

        allPosts.getContent().forEach(allPostResponseDto -> {
            int postId = allPostResponseDto.getPostId();

            allPostResponseDto.setViewsCount(allPostResponseDto.getViewsCount()+postViewCountManager.getPostViewCount(postId));
            allPostResponseDto.setCommentsCount(allPostResponseDto.getCommentsCount()+postCommentCountManager.getPostCommentCount(postId));
            allPostResponseDto.setLikesCount(allPostResponseDto.getLikesCount()+postLikeCountManager.getPostLikeCount(postId));
        });

        System.out.println("PostComentCountManager(get): "+postCommentCountManager.hashCode());
        return allPosts;
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
        GetPostDetailResponseDto getPostDetailResponseDto = postRepository.getPostByPostId(authHelper.findUserFromRequest(request).getId(), postId).orElseThrow(() -> new RestApiException(CommonErrorCode.NOT_FOUND));

        postViewCountManager.increaseViewCount(postId);

        getPostDetailResponseDto.setViewsCount(getPostDetailResponseDto.getViewsCount()+postViewCountManager.getPostViewCount(postId));
        getPostDetailResponseDto.setCommentsCount(getPostDetailResponseDto.getCommentsCount()+postCommentCountManager.getPostCommentCount(postId));
        getPostDetailResponseDto.setLikesCount(getPostDetailResponseDto.getLikesCount()+postLikeCountManager.getPostLikeCount(postId));


        return getPostDetailResponseDto;
    }

    public void updatePost(HttpServletRequest request, int postId, UpdatePostRequestDto updatePostRequestDto, List<MultipartFile> imageList) {
        if(updatePostRequestDto.getTitle().isEmpty() || updatePostRequestDto.getTitle().length()>26) throw new RestApiException(PostErrorCode.INVALID_TITLE);
        if(updatePostRequestDto.getContent().isEmpty() || updatePostRequestDto.getContent().length()>2000) throw new RestApiException(PostErrorCode.INVALID_CONTENT);

        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new RestApiException(CommonErrorCode.NOT_FOUND));

        if (!post.getUser().equals(authHelper.findUserFromRequest(request))) throw new RestApiException(CommonErrorCode.FORBIDDEN);

        post.setTitle(updatePostRequestDto.getTitle());
        post.setContent(updatePostRequestDto.getContent());

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

        if (!post.getUser().equals(authHelper.findUserFromRequest(request))) throw new RestApiException(CommonErrorCode.FORBIDDEN);

        if(post.getDeletedAt()!=null) throw new RestApiException(CommonErrorCode.BAD_REQUEST);

        post.deletePost();
    }
}
