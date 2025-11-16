package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.dto.request.ImageRequestDto;
import kr.kakao_tech_bootcamp.community.entity.Post;
import kr.kakao_tech_bootcamp.community.entity.PostImage;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.repository.post.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PostImageService {
    private final PostImageRepository postImageRepository;

    // 이미지 추가
    public List<PostImage> createPostImages(List<ImageRequestDto> imageList, Post post) {
        List<PostImage> postImageList = new ArrayList<>();
        if(imageList == null) return postImageList;

        // 이미지 리스트에서 이미지 하나씩 꺼내와 이름과 UUID+확장자 저장
        for(int i=0; i<imageList.size();i++){
            ImageRequestDto imageRequestDto = imageList.get(i);
            PostImage postImage = new PostImage(imageRequestDto.getImagePath(), imageRequestDto.getImageName(), i, post);
            postImageList.add(postImage);
        }

        return postImageRepository.saveAll(postImageList);
    }
}
