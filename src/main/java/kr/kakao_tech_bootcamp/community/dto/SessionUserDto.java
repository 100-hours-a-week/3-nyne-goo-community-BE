package kr.kakao_tech_bootcamp.community.dto;

import lombok.Getter;

// expiresAt: 사용자 활동 있을 때마다 갱신
// createdAt: absolute 만료 확인 시 사용
// lastTouchedAt: 마지막으로 세션 연장한 시각. 마지막 touch 이후 5분 이내면 갱신 안함

@Getter
public class SessionUserDto {
    private int userId;     // userId
    private long expiresAt; // 만료 시간(30분 이후)
    private long createdAt;
    private long lastTouchedAt;

    private SessionUserDto(int userId, long duration) {
        long now = System.currentTimeMillis();
        this.userId = userId;
        this.expiresAt = now + duration * 1000;
        this.createdAt = now;
        this.lastTouchedAt = now;
    }

    public static SessionUserDto of(int userId, long duration) {
        return new SessionUserDto(userId, duration);
    }

    // 만료 여부
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    // 갱신
    public void renew(long idleSeconds) {
        long now = System.currentTimeMillis();
        this.expiresAt = now + idleSeconds * 1000L;
        this.lastTouchedAt = now;
    }

    // absolute 만료 여부
    public boolean isAbsoluteExpired(long absoluteTimeoutSeconds) {
        long now = System.currentTimeMillis();
        return (now - createdAt) > absoluteTimeoutSeconds * 1000L;
    }

    // 마지막 갱신으로부터 5분 미만이면 false
    public boolean canTouch(long throttleSeconds) {
        long now = System.currentTimeMillis();
        return (now - lastTouchedAt) >= throttleSeconds * 1000L;
    }
}
