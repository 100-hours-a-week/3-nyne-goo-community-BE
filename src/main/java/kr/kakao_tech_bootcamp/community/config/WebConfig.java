package kr.kakao_tech_bootcamp.community.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${storage.upload-dir}")
    private String uploadDir;

    // 허용할 origin 추가
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontendUrl) // 허용할 출처
                .allowedMethods("GET", "POST", "PATCH", "DELETE") // 허용할 메서드
                .allowedHeaders("*") // 허용할 요청 헤더
                .allowCredentials(true) // 쿠키 허용
                .maxAge(3600); // 사전 요청(preflight) 캐시 시간
    }

    // 로컬의 uploads 디렉토리 안의 파일을 url로 접근 가능하도록 만들어줌
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uri = Paths.get(uploadDir).toUri().toString();

        registry.addResourceHandler("/uploads/**").addResourceLocations(uri);
    }

    // mime 타입
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .mediaType("jpg", MediaType.IMAGE_JPEG)
                .mediaType("jpeg", MediaType.IMAGE_JPEG)
                .mediaType("png", MediaType.IMAGE_PNG)
                .mediaType("gif", MediaType.IMAGE_GIF);
    }
}
