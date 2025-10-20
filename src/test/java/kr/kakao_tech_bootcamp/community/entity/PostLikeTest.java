package kr.kakao_tech_bootcamp.community.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostLikeTest {
    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void countTest(){
        PostLike postLikeId = new PostLike();


    }
}