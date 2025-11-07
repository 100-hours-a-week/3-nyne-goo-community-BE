package kr.kakao_tech_bootcamp.community.service;

import kr.kakao_tech_bootcamp.community.entity.PostImage;
import kr.kakao_tech_bootcamp.community.exception.RestApiException;
import kr.kakao_tech_bootcamp.community.exception.error_code.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {
    private final Path root;

    private static final Set<String> ALLOWED_EXT =
            Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    public ImageStorageService(@Value("${storage.upload-dir}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);           // 저장할 폴더 준비
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create upload dir: " + root, e);
        }
    }

    public SavedImage saveImage(MultipartFile image){
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

        try{
            Path path = root.resolve(imageUUID).normalize();     // 파일 저장 위치
            image.transferTo(path.toFile());                        // 파일 저장
            return new SavedImage(imageUUID, imageName);
        } catch (IOException e) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteImage(String imageUUID){
        if (imageUUID == null || imageUUID.isBlank()) return; // 지울 게 없으면 return

        Path target = root.resolve(imageUUID).normalize();

        // 경로 탈출 방지
        if (!target.startsWith(root)) {
            throw new RestApiException(CommonErrorCode.BAD_REQUEST);
        }

        try {
            Files.deleteIfExists(target); // 이미지 삭제
        } catch (IOException e) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public record SavedImage(String imageUUID, String imageName) {}
}
