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
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostImageService postImageService;
    private final PostViewCountManager postViewCountManager;
    private final PostCommentCountManager postCommentCountManager;
    private final PostLikeCountManager postLikeCountManager;
    private final ImageStorageService imageStorageService;

    // 모든 게시글 조회
    @Transactional(readOnly = true)
    public Slice<AllPostResponseDto> getAllPosts(int userId, Pageable pageable) {
        // DB에서 모든 게시글 받아옴
        Slice<AllPostResponseDto> allPosts = postRepository.getAllPosts(userId, pageable);

        // 매니저에서 저장된 수들 가져옴
        Map<Integer, Integer> like = postLikeCountManager.getAllPostLikeCount();
        Map<Integer, Integer> view = postViewCountManager.getAllPostViewCount();
        Map<Integer, Integer> comment = postCommentCountManager.getAllCommentsCount();

        // DB에서 가져온 값과 매니저에 저장된 값 더해서 새 객체로 리스트에 저장
        List<AllPostResponseDto> allPostResponseDtoList = allPosts.getContent().stream().map(posts -> {
            int likeCount = like.getOrDefault(posts.getPostId(), 0);
            int commentCount = comment.getOrDefault(posts.getPostId(), 0);
            int viewCount = +view.getOrDefault(posts.getPostId(), 0);

            return posts.plusCounts(posts, likeCount, commentCount, viewCount);
        }).toList();

        return new SliceImpl<>(allPostResponseDtoList, pageable, allPosts.hasNext());
    }

    // 게시글 생성
    public CreatePostResponseDto createPost(int userId, CreatePostRequestDto createPostRequestDto, List<MultipartFile> imageList) {
        System.out.println("create post request: "+createPostRequestDto.title());
        // 제목, 내용 길이 확인
        if(createPostRequestDto.title() == null || createPostRequestDto.title().length()>26) throw new RestApiException(PostErrorCode.INVALID_TITLE);
        if(createPostRequestDto.content() == null || createPostRequestDto.content().length()>2000) throw new RestApiException(PostErrorCode.INVALID_CONTENT);

        User user = userRepository.getOne(userId);
        Post post = new Post(createPostRequestDto.title(), createPostRequestDto.content(), user);

        if (imageList != null && !imageList.isEmpty()) {
            List<PostImage> postImageList = postImageService.createPostImages(imageList, post);
            post.getImages().addAll(postImageList);
        }

        return CreatePostResponseDto.from(postRepository.save(post));
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public GetPostDetailResponseDto getPostDetail(int userId, int postId) {
        GetPostDetailResponseDto postDetail = postRepository.getPostByPostId(userId, postId).orElseThrow(() -> new RestApiException(CommonErrorCode.NOT_FOUND));

        postViewCountManager.increaseViewCount(postId);

        int likeCount = postLikeCountManager.getPostLikeCount(postId);
        int commentCount = postCommentCountManager.getPostCommentCount(postId);
        int viewCount = postViewCountManager.getPostViewCount(postId);

        // 매니저에서 가져온 좋아요수, 댓글수, 조회수를 더해서 반환
        return postDetail.plusCounts(likeCount, commentCount, viewCount);
    }

    // 게시글 수정
    public void updatePost(int userId, int postId, UpdatePostRequestDto updatePostRequestDto, List<MultipartFile> imageList) {
        // 제목, 내용 길이 확인
        if(updatePostRequestDto.title().isEmpty() || updatePostRequestDto.title().length()>26) throw new RestApiException(PostErrorCode.INVALID_TITLE);
        if(updatePostRequestDto.content().isEmpty() || updatePostRequestDto.content().length()>2000) throw new RestApiException(PostErrorCode.INVALID_CONTENT);

        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new RestApiException(CommonErrorCode.NOT_FOUND));

        // 게시글 작성자와 수정하려는 사람이 다르면 forbidden 예외 처리
        if (!post.getUser().getId().equals(userId)) throw new RestApiException(CommonErrorCode.FORBIDDEN);

        post.setTitle(updatePostRequestDto.title());
        post.setContent(updatePostRequestDto.content());
        post.setUpdatedAt();

        // 기존에 저장된 이미지 리스트 삭제
        for (PostImage prevImage : post.getImages()) {
            imageStorageService.deleteImage(prevImage.getImageUUID());
        }

        post.getImages().clear();

        // 이미지 리스트 새로 저장
        List<PostImage> postImageList = postImageService.createPostImages(imageList, post);
        post.getImages().addAll(postImageList);

        postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(int userId, int postId) {
        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new RestApiException(CommonErrorCode.NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) throw new RestApiException(CommonErrorCode.FORBIDDEN);

        if(post.getDeletedAt()!=null) throw new RestApiException(CommonErrorCode.BAD_REQUEST);

        post.deletePost();
    }
}
