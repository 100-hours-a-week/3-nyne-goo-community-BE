package kr.kakao_tech_bootcamp.community.repository.user;

import kr.kakao_tech_bootcamp.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);

    @Modifying
    @Query(value = "delete from User u where u.userStatus = 'DELETED' and u.deletedAt < :cutoffDate")
    void deleteByDeletedAtBefore(LocalDateTime cutoffDate);

    /*@Modifying
    @Query(value = "DELETE FROM User u WHERE u.status = 'DELETED'")
    void deleteByUserStatus(UserStatus status);*/
}
