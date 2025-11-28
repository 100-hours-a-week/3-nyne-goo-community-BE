package kr.kakao_tech_bootcamp.community.service.user;

import kr.kakao_tech_bootcamp.community.dto.request.user.SignUpRequestDto;
import kr.kakao_tech_bootcamp.community.dto.response.user.SignUpResponseDto;
import kr.kakao_tech_bootcamp.community.entity.User;
import kr.kakao_tech_bootcamp.community.repository.RefreshTokenRepository;
import kr.kakao_tech_bootcamp.community.repository.user.UserRepository;
import kr.kakao_tech_bootcamp.community.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    // given
    @Mock
    UserRepository mockUserRepository;
    @Mock
    RefreshTokenRepository mockRefreshTokenRepository;
    @Mock
    PasswordEncoder mockPasswordEncoder;
    @InjectMocks
    UserService userService;

    @BeforeEach
    public void before() {
        System.out.println("Test Before");
    }

    @AfterEach
    public void after() {
        System.out.println("Test After");
    }

    @DisplayName("유저 생성 후 응답 확인")
    @Test
    void signupUserTest() {
        // given
        String email = "testEmail@email.com";
        String password = "testPassword1!";
        String nickname = "testName";

        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(email, password, nickname, null);

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(mockUserRepository.findByNickname(nickname)).thenReturn(Optional.empty());
        when(mockPasswordEncoder.encode(password)).thenReturn("encodedPassword");
        when(mockUserRepository.save(Mockito.any(User.class)))                      // 어떤 user 타입이든 저장할 때
                .thenAnswer(invocation -> {                         // invocation -> {}: 호출 시 정보가 담긴 객체
                    User user = invocation.getArgument(0);                        // save()에 전달된 0번 인자(user)를 꺼냄
                    ReflectionTestUtils.setField(user, "id", 1);        // private field인 id에 값(1)을 집어넣음
                    return user;
                });

        // when
        SignUpResponseDto signUpResponseDto = userService.signUp(signUpRequestDto);

        // then
        assertThat(signUpResponseDto).isNotNull();
        assertThat(signUpResponseDto.getEmail()).isEqualTo(email);
    }
}
