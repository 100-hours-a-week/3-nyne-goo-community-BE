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
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PostImageService {
    private final PostImageRepository postImageRepository;

    private static final Set<String> ALLOWED_EXT = Set.of(".jpg", ".jpeg", ".png");

    // 이미지 추가
    public List<PostImage> createPostImages(List<MultipartFile> imageList, Post post) {
        List<PostImage> postImageList = new ArrayList<>();
        if(imageList == null) return postImageList;

        // 이미지 리스트에서 이미지 하나씩 꺼내와 이름과 UUID+확장자 저장
        for(int i=0; i<imageList.size();i++){
            MultipartFile image = imageList.get(i);
            String imageName = image.getOriginalFilename();

            // 파일 이름 없을 때
            if(imageName==null || imageName.isBlank()) throw new RestApiException(CommonErrorCode.BAD_REQUEST);
            int dot = imageName.lastIndexOf('.');

            // 확장자 없을 때
            if(dot<0 || dot == imageName.length()-1) throw new RestApiException(CommonErrorCode.BAD_REQUEST);
            String ext = imageName.substring(dot).toLowerCase(Locale.ROOT);

            // 허용되지 않은 확장자일때
            if(!ALLOWED_EXT.contains(ext)) throw new RestApiException(CommonErrorCode.BAD_REQUEST);
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
