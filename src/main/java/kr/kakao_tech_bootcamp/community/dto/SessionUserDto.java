package kr.kakao_tech_bootcamp.community.dto;

import lombok.Getter;

@Getter
public class SessionUserDto {
    int userId;     // userId
    long expiresAt; // 만료 시간

    private SessionUserDto(int userId, long duration) {
        this.userId = userId;
        this.expiresAt = System.currentTimeMillis() + duration * 1000;
    }

    public static SessionUserDto of(int userId, long duration){
        return new SessionUserDto(userId, duration);
    }
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public void renew(long durationSeconds) {
        this.expiresAt = System.currentTimeMillis() + durationSeconds * 1000;
    }
}
