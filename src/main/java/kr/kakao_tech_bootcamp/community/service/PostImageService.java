package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.entity.Post;
import kr.kakao_tech_bootcamp.community.entity.PostImage;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import kr.kakao_tech_bootcamp.community.repository.post.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PostImageService {
    private final PostImageRepository postImageRepository;

    // 이미지 추가
    public List<PostImage> createPostImages(List<MultipartFile> imageList, Post post) {
        List<PostImage> postImageList = new ArrayList<>();
        if(imageList == null) return postImageList;

        // 이미지 리스트에서 이미지 하나씩 꺼내와 이름과 UUID+확장자 저장
        for(int i=0; i<imageList.size();i++){
            MultipartFile image = imageList.get(i);
            String imageName = image.getOriginalFilename();
            String ext = imageName.substring(imageName.lastIndexOf("."));
            String imageUUID = UUID.randomUUID() + ext;

            PostImage postImage = new PostImage(imageUUID, imageName, i, post);
            postImageList.add(postImage);

            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            try{
                Files.createDirectories(Paths.get(uploadDir));          // 저장할 폴더 준비
                Path path = Paths.get(uploadDir + imageUUID);      // 파일 저장 위치 (전체 경로)
                image.transferTo(path.toFile());                        // 파일 저장
            } catch (IOException e) {
                throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        return postImageRepository.saveAll(postImageList);
    }
}
