package kr.kakao_tech_bootcamp.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")     // application-test.yml 설정 우선 적용
class CommunityApplicationTests {

	@Test
	void contextLoads() {
	}

}
