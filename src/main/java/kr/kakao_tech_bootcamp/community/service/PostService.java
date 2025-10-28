package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.dto.SessionUserDto;
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
import org.hibernate.Session;
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
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageService postImageService;
    private final PostViewCountManager postViewCountManager;
    private final PostCommentCountManager postCommentCountManager;
    private final PostLikeCountManager postLikeCountManager;

    public Slice<AllPostResponseDto> getAllPosts(SessionUserDto sessionUserDto, Pageable pageable) {
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        Slice<AllPostResponseDto> allPosts = postRepository.getAllPosts(user.getId(), pageable);

        allPosts.getContent().forEach(allPostResponseDto -> {
            int postId = allPostResponseDto.getPostId();

            allPostResponseDto.setViewsCount(allPostResponseDto.getViewsCount() + postViewCountManager.getPostViewCount(postId));
            allPostResponseDto.setCommentsCount(allPostResponseDto.getCommentsCount() + postCommentCountManager.getPostCommentCount(postId));
            allPostResponseDto.setLikesCount(allPostResponseDto.getLikesCount() + postLikeCountManager.getPostLikeCount(postId));
        });

        System.out.println("PostComentCountManager(get): " + postCommentCountManager.hashCode());
        return allPosts;
    }

    public CreatePostResponseDto createPost(SessionUserDto sessionUserDto, CreatePostRequestDto createPostRequestDto, List<MultipartFile> imageList) {
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        if (createPostRequestDto.getTitle().isEmpty()) throw new RestApiException(PostErrorCode.EMPTY_TITLE);
        if (createPostRequestDto.getContent().isEmpty()) throw new RestApiException(PostErrorCode.EMPTY_CONTENT);

        if (createPostRequestDto.getTitle().length() > 26) throw new RestApiException(PostErrorCode.TOO_LONG_TITLE);
        if (createPostRequestDto.getContent().length() > 2000)
            throw new RestApiException(PostErrorCode.TOO_LONG_CONTENT);

        Post post = new Post(createPostRequestDto.getTitle(), createPostRequestDto.getContent(), user);

        if (imageList != null && !imageList.isEmpty()) {
            List<PostImage> postImageList = postImageService.createPostImages(imageList, post);
            post.getImages().addAll(postImageList);
        }

        return CreatePostResponseDto.from(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public GetPostDetailResponseDto getPostDetail(SessionUserDto sessionUserDto, int postId) {
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        GetPostDetailResponseDto getPostDetailResponseDto = postRepository.getPostByPostId(user.getId(), postId).orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));

        postViewCountManager.increaseViewCount(postId);

        getPostDetailResponseDto.setViewsCount(getPostDetailResponseDto.getViewsCount() + postViewCountManager.getPostViewCount(postId));
        getPostDetailResponseDto.setCommentsCount(getPostDetailResponseDto.getCommentsCount() + postCommentCountManager.getPostCommentCount(postId));
        getPostDetailResponseDto.setLikesCount(getPostDetailResponseDto.getLikesCount() + postLikeCountManager.getPostLikeCount(postId));


        return getPostDetailResponseDto;
    }

    public void updatePost(SessionUserDto sessionUserDto, int postId, UpdatePostRequestDto updatePostRequestDto, List<MultipartFile> imageList) {
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        if (updatePostRequestDto.getTitle().isEmpty()) throw new RestApiException(PostErrorCode.EMPTY_TITLE);
        if (updatePostRequestDto.getContent().isEmpty()) throw new RestApiException(PostErrorCode.EMPTY_CONTENT);

        if (updatePostRequestDto.getTitle().length() > 26) throw new RestApiException(PostErrorCode.TOO_LONG_TITLE);
        if (updatePostRequestDto.getContent().length() > 2000)
            throw new RestApiException(PostErrorCode.TOO_LONG_CONTENT);

        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));

        if (!post.getUser().equals(user)) throw new RestApiException(CommonErrorCode.FORBIDDEN);

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

    public void deletePost(SessionUserDto sessionUserDto, int postId) {
        if(sessionUserDto==null) throw new RestApiException(CommonErrorCode.UNAUTHORIZED);
        User user = userRepository.getReferenceById(sessionUserDto.getUserId());
        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new RestApiException(PostErrorCode.POST_NOT_FOUND));

        if (!post.getUser().equals(user)) throw new RestApiException(CommonErrorCode.FORBIDDEN);

        if (post.getDeletedAt() != null) throw new RestApiException(PostErrorCode.ALREADY_DELETED);

        postRepository.delete(post);
    }
}
