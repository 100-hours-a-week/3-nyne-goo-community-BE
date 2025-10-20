package kr.kakao_tech_bootcamp.community.service;

import jakarta.persistence.EntityNotFoundException;
import kr.kakao_tech_bootcamp.community.dto.request.post.CreatePostRequestDto;
import kr.kakao_tech_bootcamp.community.dto.request.post.UpdatePostRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.GetPostDetailResponseDto;
import kr.kakao_tech_bootcamp.community.entity.*;
import kr.kakao_tech_bootcamp.community.exception.ForbiddenException;
import kr.kakao_tech_bootcamp.community.exception.UnauthorizedException;
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
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PostImageService postImageService;
    private final PostViewCountManager postViewCountManager;
    private final PostCommentCountManager postCommentCountManager;
    private final PostLikeCountManager postLikeCountManager;

    public Slice<AllPostResponseDto> getAllPosts(String token, Pageable pageable) {
        int userId = jwtProvider.getIdFromToken(token);
        Slice<AllPostResponseDto> allPosts = postRepository.getAllPosts(userId, pageable);

        allPosts.getContent().forEach(allPostResponseDto -> {
            int postId = allPostResponseDto.getPostId();

            allPostResponseDto.setViewsCount(allPostResponseDto.getViewsCount()+postViewCountManager.getPostViewCount(postId));
            allPostResponseDto.setCommentsCount(allPostResponseDto.getCommentsCount()+postCommentCountManager.getPostCommentCount(postId));
            allPostResponseDto.setLikesCount(allPostResponseDto.getLikesCount()+postLikeCountManager.getPostLikeCount(postId));
        });

        System.out.println("PostComentCountManager(get): "+postCommentCountManager.hashCode());
        return allPosts;
    }

    public Post createPost(String token, CreatePostRequestDto createPostRequestDto, List<MultipartFile> imageList) {
        int userId = jwtProvider.getIdFromToken(token);

        User user = userRepository.getReferenceById(userId);
        Post post = new Post(createPostRequestDto.getTitle(), createPostRequestDto.getContent(), user);

        if (imageList != null && !imageList.isEmpty()) {
            List<PostImage> postImageList = postImageService.createPostImages(imageList, post);
            post.getImages().addAll(postImageList);
        }

        postRepository.save(post);
        return post;
    }

    @Transactional(readOnly = true)
    public GetPostDetailResponseDto getPostDetail(String token, int postId) {
        int userId = jwtProvider.getIdFromToken(token);
        GetPostDetailResponseDto getPostDetailResponseDto = postRepository.getPostByPostId(userId, postId).orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        postViewCountManager.increaseViewCount(postId);

        getPostDetailResponseDto.setViewsCount(getPostDetailResponseDto.getViewsCount()+postViewCountManager.getPostViewCount(postId));
        getPostDetailResponseDto.setCommentsCount(getPostDetailResponseDto.getCommentsCount()+postCommentCountManager.getPostCommentCount(postId));
        getPostDetailResponseDto.setLikesCount(getPostDetailResponseDto.getLikesCount()+postLikeCountManager.getPostLikeCount(postId));


        return getPostDetailResponseDto;
    }

    public void updatePost(String token, int postId, UpdatePostRequestDto updatePostRequestDto, List<MultipartFile> imageList) {
        int userId = jwtProvider.getIdFromToken(token);
        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(userId)) throw new ForbiddenException();

        post.setTitle(updatePostRequestDto.getTitle());
        post.setContent(updatePostRequestDto.getContent());

        for (PostImage prevImage : post.getImages()) {
            Path path = Paths.get(System.getProperty("user.dir") + "/uploads/" + prevImage.getImageUUID());
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("이미지 파일 삭제 실패: " + path + "\ner ror:" + e.getMessage());
            }
        }

        post.getImages().clear();
        List<PostImage> postImageList = postImageService.createPostImages(imageList, post);
        post.getImages().addAll(postImageList);

        postRepository.save(post);
    }

    public void deletePost(String token, int postId) {
        int userId = jwtProvider.getIdFromToken(token);
        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(userId)) throw new ForbiddenException();

        if(post.getDeletedAt()!=null) throw new IllegalStateException("이미 삭제한 게시글입니다.");

        postRepository.delete(post);
    }
}
