package kr.kakao_tech_bootcamp.community.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import kr.kakao_tech_bootcamp.community.UserStatus;
import kr.kakao_tech_bootcamp.community.dto.request.user.SignUpRequestDto;
import lombok.Getter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET status = 'DELETED', deleted_at = NOW() WHERE id = ?")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private UserStatus userStatus;

    @Column(name = "image_uuid")
    private String imageUUID;

    @Column(name = "image_original_name")
    private String imageName;

    protected User() {
    }

    public User(SignUpRequestDto signUpRequestDto, String imageUUID, String imageName) {
        this.email = signUpRequestDto.getEmail();
        this.nickname = signUpRequestDto.getNickname();
        this.password = signUpRequestDto.getPassword();
        this.createdAt = LocalDateTime.now();
        this.userStatus = UserStatus.ACTIVE;

        this.imageUUID = imageUUID;
        this.imageName = imageName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setImage(String imageUUID, String imageName) {
        this.imageUUID = imageUUID;
        this.imageName = imageName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void deleteUser(){
        this.deletedAt = LocalDateTime.now();
        this.userStatus = UserStatus.DELETED;
    }
}
