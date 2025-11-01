package kr.kakao_tech_bootcamp.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    @Column(unique = true)
    private String refreshToken;

    private Instant expiresAt;

    public RefreshToken(int userId, String refreshToken, Instant expiresAt) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken of(int userId, String refreshToken, Instant expiresAt) {
        return new RefreshToken(userId, refreshToken, expiresAt);
    }
}
